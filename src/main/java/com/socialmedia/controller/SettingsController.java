package com.socialmedia.controller;

import com.socialmedia.entity.Notification;
import com.socialmedia.entity.User;
import com.socialmedia.service.NotificationService;
import com.socialmedia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class SettingsController {
    private final UserService userService;
    private final NotificationService notificationService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SettingsController(UserService userService, NotificationService notificationService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/settings")
    public String getSettingsPage(Model model) {

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

        return "settings";
    }

    @PostMapping("/changeUserInfo")
    public String setUserInfo(Model model,
                              @RequestParam("email") String email,
                              @RequestParam("username") String username,
                              @RequestParam("isPrivate") boolean isPrivate) {
        Optional<User> userByEmail = userService.getUserByEmail(email);
        Optional<User> userByUsername = userService.getUserByUsername(username);
        User user = userService.getCurrentUser();

        boolean emailTakenByOther = userByEmail.isPresent() && !userByEmail.get().equals(user);
        boolean usernameTakenByOther = userByUsername.isPresent() && !userByUsername.get().equals(user);

        if (!emailTakenByOther && !usernameTakenByOther) {
            user.setEmail(email);
            user.setUsername(username);
            user.setIsPrivate(isPrivate);
            userService.updateUseInfo(user);
            return "redirect:/settings";
        }

        if (emailTakenByOther)
            model.addAttribute("error", "This email is already registered, please try another mail address.");
        else model.addAttribute("error", "This username is already taken, please try another username.");

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

        model.addAttribute("user", user);

        return "settings";
    }

    @PostMapping("/changeUserPassword")
    public String setUserPassword(Model model,
                                  @RequestParam("password") String password,
                                  @RequestParam("new_password") String newPassword,
                                  @RequestParam("confirm_password") String confirmPassword) {

        User user = userService.getCurrentUser();

        if (Objects.equals(newPassword, confirmPassword)) {
            boolean matches = passwordEncoder.matches(password, user.getPassword());
            if (matches) {
                userService.updatePassword(user, newPassword);
                return "redirect:/home";
            } else model.addAttribute("error", "Please enter your current password.");
        } else {
            model.addAttribute("error", "Password fields don't match.");
        }

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

        model.addAttribute("user", user);

        return "settings";
    }
}
