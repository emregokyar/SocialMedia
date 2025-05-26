package com.socialmedia.controller;

import com.socialmedia.entity.Notification;
import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import com.socialmedia.service.MessageService;
import com.socialmedia.service.NotificationService;
import com.socialmedia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AboutController {
    private final UserService userService;
    private final NotificationService notificationService;
    private final MessageService messageService;

    @Autowired
    public AboutController(UserService userService, NotificationService notificationService, MessageService messageService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.messageService = messageService;
    }

    @GetMapping("/about")
    public String getAboutPage(Model model) {
        Object currentUser = userService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);
            model.addAttribute("currentUsername", currentUsername);
        }
        model.addAttribute("currentUser", currentUser);

        List<Notification> notificationList = notificationService.getAllNotificationsUserNotSeen();
        int notificationCount = notificationList.size();
        model.addAttribute("notificationCount", notificationCount);

        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("newPhoto", new Photo());

        Integer messageCount = messageService.countMessages(user);
        model.addAttribute("messageCount", messageCount);
        return "about";
    }
}
