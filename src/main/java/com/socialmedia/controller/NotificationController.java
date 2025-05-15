package com.socialmedia.controller;

import com.socialmedia.entity.*;
import com.socialmedia.service.CommentService;
import com.socialmedia.service.FollowService;
import com.socialmedia.service.NotificationService;
import com.socialmedia.service.UserService;
import com.socialmedia.util.FollowStatus;
import com.socialmedia.util.NotificationTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class NotificationController {
    private final UserService userService;
    private final NotificationService notificationService;
    private final CommentService commentService;
    private final FollowService followService;

    @Autowired
    public NotificationController(UserService userService, NotificationService notificationService, CommentService commentService, FollowService followService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.commentService = commentService;
        this.followService = followService;
    }

    @GetMapping("/notifications")
    public String getNotifications(Model model) {
        Object currentUser = userService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);
            model.addAttribute("currentUsername", currentUsername);
        }
        model.addAttribute("user", currentUser);

        //Retrieving all notifications that user has not seen
        List<Notification> notifications = notificationService.getAllNotificationsUserNotSeen();
        model.addAttribute("notifications", notifications);

        for (Notification notification : notifications) {
            notification.setRead(true);
            notificationService.savePlainNotification(notification);
        }

        //Retrieving photo id's and match them with typed id's so user can reach post that other's commented
        Map<Integer, Integer> photosMap = new HashMap<>();
        Map<Integer, Boolean> followSituations = new HashMap<>();
        for (var ntf : notifications) {
            if (ntf.getTypes().equals(NotificationTypes.COMMENT)) {
                Comment commentById = commentService.getCommentById(ntf.getTypedId());
                Integer photoId = commentById.getPhoto().getId();
                photosMap.put(ntf.getTypedId(), photoId);
            } else if (ntf.getTypes().equals(NotificationTypes.FOLLOW)) {
                Optional<Follow> follow = followService.findFollowByFollowerAndFollowee(ntf.getSender(), ntf.getReceiver());
                if (follow.isPresent()) {
                    boolean userFollowing = (follow.get().getStatus() == FollowStatus.APPROVED);
                    followSituations.put(ntf.getSender().getId(), userFollowing);
                }
                followSituations.put(ntf.getSender().getId(), false);
            }
        }
        model.addAttribute("photosMap", photosMap);
        model.addAttribute("current", userService.getCurrentUser());
        model.addAttribute("followSituations", followSituations);

        model.addAttribute("newPhoto", new Photo());
        return "notification-page";
    }

    @PostMapping("/acceptRequest")
    public String acceptRequest(@RequestParam("userId") int senderId,
                                @RequestParam("notificationId") int notificationId) {
        User sender = userService.getUserById(senderId);
        User currentUser = userService.getCurrentUser();

        Optional<Notification> notification = notificationService.findNtfById(notificationId);
        Optional<Follow> follow = followService.findFollowByFollowerAndFollowee(sender, currentUser);
        if (notification.isPresent()) {
            if (follow.get().getStatus() == FollowStatus.PENDING) {
                follow.get().setStatus(FollowStatus.APPROVED);
                followService.plainSaveFollow(follow.get());

                notification.ifPresent(value -> value.setMessage(sender.getUsername() + " started following you."));
                notificationService.savePlainNotification(notification.get());
            }
        }
        return "redirect:/notifications";
    }

    @PostMapping("/rejectRequest")
    public String rejectRequest(@RequestParam("userId") int senderId,
                                @RequestParam("notificationId") int notificationId) {
        User sender = userService.getUserById(senderId);
        User currentUser = userService.getCurrentUser();

        Optional<Notification> notification = notificationService.findNtfById(notificationId);
        Optional<Follow> follow = followService.findFollowByFollowerAndFollowee(sender, currentUser);
        follow.ifPresent(followService::deletePlainFollow);
        notification.ifPresent(notificationService::deleteNotifications);

        return "redirect:/notifications";
    }

    @PostMapping("/clearNotification")
    public String deleteSingleNotification(@RequestParam("notificationId") int notificationId) {
        Optional<Notification> notification = notificationService.findNtfById(notificationId);
        if (notification.get().getTypes().equals(NotificationTypes.FOLLOW)) {
            Optional<Follow> follow = followService.findFollowByFollowerAndFollowee(notification.get().getSender(), notification.get().getReceiver());
            if (follow.isPresent()) {
                if (!follow.get().getStatus().equals(FollowStatus.APPROVED)) {
                    followService.deletePlainFollow(follow.get());
                }
            }
        }
        notification.ifPresent(notificationService::deleteNotifications);
        return "redirect:/notifications";
    }

    @PostMapping("/clearAllNotifications")
    public String clearAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();

        if (!notifications.isEmpty()) {
            for (Notification notification : notifications) {
                if (notification.getTypes().equals(NotificationTypes.FOLLOW)) {
                    Optional<Follow> follow = followService.findFollowByFollowerAndFollowee(notification.getSender(), notification.getReceiver());
                    if (follow.get().getStatus().equals(FollowStatus.APPROVED))
                        notificationService.deleteNotifications(notification);
                    //Clearing all notifications but user hasn't accepted requests
                }
                notificationService.deleteNotifications(notification);
            }
        }

        return "redirect:/notifications";
    }

    @GetMapping("/seeAllNotifications")
    public String seeAllNotifications(Model model) {

        Object currentUser = userService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);
            model.addAttribute("currentUsername", currentUsername);
            model.addAttribute("user", currentUser);
        }
        //GetAll Notifications that user has read
        List<Notification> notifications = notificationService.getAllNotifications();
        model.addAttribute("notifications", notifications);

        //Retrieving photo id's and match them with typed id's so user can reach post that other's commented
        Map<Integer, Integer> photosMap = new HashMap<>();
        Map<Integer, Boolean> followSituations = new HashMap<>();
        for (var ntf : notifications) {
            if (ntf.getTypes().equals(NotificationTypes.COMMENT)) {
                Comment commentById = commentService.getCommentById(ntf.getTypedId());
                Integer photoId = commentById.getPhoto().getId();
                photosMap.put(ntf.getTypedId(), photoId);
            } else if (ntf.getTypes().equals(NotificationTypes.FOLLOW)) {
                Optional<Follow> follow = followService.findFollowByFollowerAndFollowee(ntf.getSender(), ntf.getReceiver());
                boolean userFollowing = followService.isUserFollowing(ntf.getSender(), ntf.getReceiver()) && (follow.get().getStatus() == FollowStatus.APPROVED);
                followSituations.put(ntf.getSender().getId(), userFollowing);
            }
        }
        model.addAttribute("photosMap", photosMap);
        model.addAttribute("current", userService.getCurrentUser());
        model.addAttribute("followSituations", followSituations);
        model.addAttribute("newPhoto", new Photo());
        return "notification-page";
    }
}
