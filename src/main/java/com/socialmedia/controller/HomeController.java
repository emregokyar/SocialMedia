package com.socialmedia.controller;

import com.socialmedia.entity.*;
import com.socialmedia.service.*;
import com.socialmedia.util.FileUploadUtil;
import com.socialmedia.util.FollowStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class HomeController {
    private final UserService userService;
    private final PhotoService photoService;
    private final LikeService likeService;
    private final NotificationService notificationService;
    private final UniversalTagService universalTagService;
    private final PhotoTagService photoTagService;
    private final RegularService regularService;
    private final FollowService followService;

    @Autowired
    public HomeController(UserService userService, PhotoService photoService, LikeService likeService, NotificationService notificationService, UniversalTagService universalTagService, PhotoTagService photoTagService, RegularService regularService, FollowService followService) {
        this.userService = userService;
        this.photoService = photoService;
        this.likeService = likeService;
        this.notificationService = notificationService;
        this.universalTagService = universalTagService;
        this.photoTagService = photoTagService;
        this.regularService = regularService;
        this.followService = followService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        Object currentUser = userService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);
            model.addAttribute("currentUsername", currentUsername);
        }
        model.addAttribute("user", currentUser);
        model.addAttribute("newPhoto", new Photo());


        List<Photo> photos = photoService.getFollowingPosts();
        List<Photo> popularPhotos = photoService.getMostLikedPosts(30); //Retrieving popular photos at last 7 days
        popularPhotos.removeAll(photos);//Making sure there is no duplicate
        while (photos.size() < 50) {
            popularPhotos.forEach(photo -> photos.add(photo));
            break;
        } //photos that supposed to display less than 50 photos than add photo until it reaches 50 or end of the popular photo list
        while (photos.size() < 50) {
            photoService.getLikedPosts().forEach(photo -> photos.add(photo));
            break;
        } //if still number of posts is under 50 add photos from user liked
        model.addAttribute("photos", photos);

        Map<Integer, Boolean> hasLiked = new HashMap<>();
        for (Photo photo : photos) {
            boolean userLiked = likeService.isUserLiked(photo);
            if (userLiked) hasLiked.put(photo.getId(), true);
            else hasLiked.put(photo.getId(), false);
        }
        model.addAttribute("hasLiked", hasLiked);

        List<Notification> notificationList = notificationService.getAllNotificationsUserNotSeen();
        int notificationCount = notificationList.size();
        model.addAttribute("notificationCount", notificationCount);


        Regular searcher = userService.getCurrentUser().getRegular();
        List<Regular> recommendedUsers = new ArrayList<>(regularService.searchUsers("").stream().limit(7).toList()); //Dummy list change this later
        if (recommendedUsers.contains(searcher)) recommendedUsers.remove(searcher);

        //Depends on the following statue changing statue names
        Map<Integer, String> followStatus = new HashMap<>();
        User user = userService.getCurrentUser();
        for (Regular searched : recommendedUsers) {
            User targetUser = searched.getUser();
            Optional<Follow> foundFollow = followService.findFollowByFollowerAndFollowee(user, targetUser);
            if (foundFollow.isPresent()) {
                FollowStatus status = foundFollow.get().getStatus();
                if (status == FollowStatus.APPROVED) {
                    followStatus.put(targetUser.getId(), "Following");
                } else if (status == FollowStatus.PENDING) {
                    followStatus.put(targetUser.getId(), "Pending");
                } else {
                    followStatus.put(targetUser.getId(), "Follow");
                }
            } else {
                followStatus.put(targetUser.getId(), "Follow");
            }
        }
        model.addAttribute("recommendedUsers", recommendedUsers);
        model.addAttribute("followStatus", followStatus);

        return "home";
    }

    @PostMapping("/shareNewPost")
    public String shareNewPost(@ModelAttribute("photo") @Valid Photo photo, @RequestParam("image") MultipartFile image) {
        User currentUser = userService.getCurrentUser();

        String extension = "";
        if (!Objects.equals(image.getOriginalFilename(), "")) {
            String orgName = image.getOriginalFilename();
            assert orgName != null;
            extension = orgName.substring(orgName.lastIndexOf('.'));
        }
        Photo savedPhoto = photoService.savePhoto(photo, currentUser, extension);
        String photoName = savedPhoto.getId().toString() + extension;

        try {
            String uploadDir = "photos/users/" + currentUser.getId() + "/photos";
            if (!Objects.equals(image.getOriginalFilename(), "")) {
                FileUploadUtil.saveFile(uploadDir, photoName, image);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Parse tags here if it is available
        Pattern pattern = Pattern.compile("#(\\w+)");
        Matcher matcher = pattern.matcher(savedPhoto.getCaption());
        while (matcher.find()) {
            UniversalTag newTag = new UniversalTag();

            String value = matcher.group(1);// with group(1) skipping # part
            Optional<UniversalTag> tag = universalTagService.findByTagName(value);

            if (tag.isEmpty()) {
                newTag.setTagName(value);
                newTag = universalTagService.saveTag(newTag);
                PhotoTag photoTag = new PhotoTag();
                photoTag.setPhoto(savedPhoto);
                photoTag.setUniversalTag(newTag);
                PhotoTagId id = new PhotoTagId(savedPhoto.getId(), newTag.getId());
                photoTag.setId(id);
                photoTagService.savePhotoTag(photoTag);
            } else {
                if (!photoTagService.isExists(savedPhoto, tag.get())) {
                    PhotoTag photoTag = new PhotoTag();
                    photoTag.setUniversalTag(tag.get());
                    photoTag.setPhoto(savedPhoto);
                    PhotoTagId id = new PhotoTagId(savedPhoto.getId(), tag.get().getId());
                    photoTag.setId(id);
                    photoTagService.savePhotoTag(photoTag);
                }
            }
        }

        return "redirect:/home";
    }
}
