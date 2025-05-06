package com.socialmedia.controller;

import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import com.socialmedia.service.FollowService;
import com.socialmedia.service.LikeService;
import com.socialmedia.service.PhotoService;
import com.socialmedia.service.UserService;
import com.socialmedia.util.FileUploadUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
public class HomeController {
    private final UserService userService;
    private final PhotoService photoService;
    private final FollowService followService;
    private final LikeService likeService;

    @Autowired
    public HomeController(UserService userService, PhotoService photoService, FollowService followService, LikeService likeService) {
        this.userService = userService;
        this.photoService = photoService;
        this.followService = followService;
        this.likeService = likeService;
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
        model.addAttribute("photo", new Photo());

        List<Photo> photos = photoService.getFollowingPosts();
        List<Photo> popularPhotos = photoService.getMostLikedPosts(30); //Retrieving popular photos at last 7 days
        popularPhotos.removeAll(photos);//Making sure there is no duplicate
        while (photos.size() < 50) {
            popularPhotos.forEach(photo ->
                    photos.add(photo));
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
        return "redirect:/home";
    }
}
