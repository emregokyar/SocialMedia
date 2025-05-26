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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller
public class RegularProfileController {
    private final RegularService regularService;
    private final UserService userService;
    private final FollowService followService;
    private final NotificationService notificationService;
    private final LikeService likeService;
    private final MessageService messageService;

    @Autowired
    public RegularProfileController(RegularService regularService, UserService userService, FollowService followService, NotificationService notificationService, LikeService likeService, MessageService messageService) {
        this.regularService = regularService;
        this.userService = userService;
        this.followService = followService;
        this.notificationService = notificationService;
        this.likeService = likeService;
        this.messageService = messageService;
    }

    @GetMapping("/profile")
    public String getUserProfile(Model model) {
        Object currentUser = userService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);
            model.addAttribute("currentUsername", currentUsername);
        }
        model.addAttribute("user", currentUser);

        User user = userService.getCurrentUser();
        List<Photo> photos = user.getPhotos();
        photos.sort((photo1, photo2) -> {
            if (photo1.getRegistrationDate().before(photo2.getRegistrationDate())) {
                return 1;
            } else if (photo2.getRegistrationDate().before(photo1.getRegistrationDate())) {
                return -1;
            }
            return 0;
        });
        model.addAttribute("photos", photos);
        model.addAttribute("postCount", photos.size());


        int followingCount = followService.getFollowers(FollowStatus.APPROVED, user.getId()).size();
        int followersCount = followService.getFollowees(FollowStatus.APPROVED, user.getId()).size();
        model.addAttribute("followersCount", followersCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("newPhoto", new Photo());

        List<User> followers = new ArrayList<>();
        List<Follow> follows = user.getFollowees();
        for (var follower : follows) {
            followers.add(follower.getFollower());
        }

        List<User> followees = new ArrayList<>();
        List<Follow> followList = user.getFollowers();
        for (var followee : followList) {
            followees.add(followee.getFollowee());
        }

        model.addAttribute("followers", followers);
        model.addAttribute("followees", followees);

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

        Integer messageCount = messageService.countMessages(user);
        model.addAttribute("messageCount", messageCount);

        return "user-profile";
    }

    @GetMapping("/editProfile")
    public String editUserProfile(Model model) {
        Object currentUser = userService.getCurrentUserProfile();
        model.addAttribute("user", currentUser);

        User user = userService.getCurrentUser();
        model.addAttribute("username", user.getUsername());

        Regular regularUser = new Regular();
        Optional<Regular> regularProfile = regularService.getOne(user.getId());
        if (regularProfile.isPresent()) {
            regularUser = regularProfile.get();
        }
        model.addAttribute("regularUser", regularUser);


        model.addAttribute("newPhoto", new Photo());

        List<Notification> notificationList = notificationService.getAllNotificationsUserNotSeen();
        int notificationCount = notificationList.size();
        model.addAttribute("notificationCount", notificationCount);

        Integer messageCount = messageService.countMessages(user);
        model.addAttribute("messageCount", messageCount);
        return "edit-profile";
    }

    @PostMapping("/saveProfile")
    public String saveUserProfile(Regular regularUser,
                                  Model model,
                                  @RequestParam("image") MultipartFile profileImage) {
        User user = userService.getCurrentUser();
        regularUser.setUser(user);
        model.addAttribute("regularUser", regularUser);

        String imageName = "";
        if (!Objects.equals(profileImage.getOriginalFilename(), "")) {
            imageName = StringUtils.cleanPath(Objects.requireNonNull(profileImage.getOriginalFilename()));
            regularUser.setProfilePhoto(imageName);
        }
        Regular regularProfile = regularService.saveRegularUser(regularUser);

        try {
            String uploadDir = "photos/users/" + regularProfile.getUserId() + "/profile_photos";
            if (!Objects.equals(profileImage.getOriginalFilename(), "")) {
                FileUploadUtil.saveFile(uploadDir, imageName, profileImage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/home";
    }

    /* Add this method when I create settings area also create part to change username, email and password
    @DeleteMapping("/deleteAccount")
    public String deleteAccount() {
        User currentUser = userService.getCurrentUser();
        String deleteDir = "photos/users/" + currentUser.getId();
        try {
            FileDeleteUtil.deleteFile(deleteDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        userService.delete(currentUser);
        return "redirect:/";
    }
     */
}
