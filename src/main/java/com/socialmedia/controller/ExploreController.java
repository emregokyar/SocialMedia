package com.socialmedia.controller;

import com.socialmedia.entity.Notification;
import com.socialmedia.entity.Photo;
import com.socialmedia.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ExploreController {
    private final UserService userService;
    private final PhotoService photoService;
    private final NotificationService notificationService;
    private final LikeService likeService;
    private final MessageService messageService;

    @Autowired
    public ExploreController(UserService userService, PhotoService photoService, NotificationService notificationService, LikeService likeService, MessageService messageService) {
        this.userService = userService;
        this.photoService = photoService;
        this.notificationService = notificationService;
        this.likeService = likeService;
        this.messageService = messageService;
    }

    @GetMapping("/explore")
    public String getAllSuggestions(
            Model model) {
        Object currentUser = userService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);
            model.addAttribute("user", currentUser);
        }

        List<Photo> photos1 = photoService.getPhotosSearchedTagAndLocation(""); //Check here later I am using dummy data
        List<Photo> photos2 = photoService.getPhotosUserFollowingDependsOnLocationAndTag(""); //Check here later I am using dummy data
        List<Photo> photos = new ArrayList<>();

        photos1.stream().limit(15)
                .forEach(photos::add);
        photos2.stream().limit(5)
                .forEach(photos::add);

        model.addAttribute("photos", photos);

        List<Notification> notificationList = notificationService.getAllNotificationsUserNotSeen();
        int notificationCount = notificationList.size();
        model.addAttribute("notificationCount", notificationCount);
        model.addAttribute("newPhoto", new Photo());

        Map<Integer, Boolean> hasLiked = new HashMap<>();
        for (Photo photo : photos) {
            boolean userLiked = likeService.isUserLiked(photo);
            if (userLiked) hasLiked.put(photo.getId(), true);
            else hasLiked.put(photo.getId(), false);
        }
        model.addAttribute("hasLiked", hasLiked);

        Integer messageCount = messageService.countMessages(userService.getCurrentUser());
        model.addAttribute("messageCount", messageCount);

        return "explore";
    }
}
