package com.socialmedia.controller;

import com.socialmedia.entity.*;
import com.socialmedia.service.*;
import com.socialmedia.util.FollowStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
public class SearchController {
    private final UserService userService;
    private final RegularService regularService;
    private final FollowService followService;
    private final PhotoService photoService;
    private final NotificationService notificationService;
    private final LikeService likeService;

    @Autowired
    public SearchController(UserService userService, RegularService regularService, FollowService followService, PhotoService photoService, NotificationService notificationService, LikeService likeService) {
        this.userService = userService;
        this.regularService = regularService;
        this.followService = followService;
        this.photoService = photoService;
        this.notificationService = notificationService;
        this.likeService = likeService;
    }

    @GetMapping("/searchUser")
    public String searchUser(Model model, @RequestParam("input") String input) {
        Object currentUser = userService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);
            model.addAttribute("user", currentUser);
        }
        Regular searcher = regularService.getOne(userService.getCurrentUser().getId()).orElseThrow(() ->
                new RuntimeException("Can not find the currentUser"));

        List<Regular> searchedUsers = regularService.searchUsers(input);
        searchedUsers.remove(searcher);//Removing searcher itself

        //Depends on the following statue changing statue names
        Map<Integer, String> followStatus = new HashMap<>();
        User user = userService.getCurrentUser();
        for (Regular searched : searchedUsers) {
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

        model.addAttribute("searchedUsers", searchedUsers);
        model.addAttribute("followStatus", followStatus);
        model.addAttribute("oldInput", input);
        model.addAttribute("newPhoto", new Photo());

        List<Notification> notificationList = notificationService.getAllNotificationsUserNotSeen();
        int notificationCount = notificationList.size();
        model.addAttribute("notificationCount", notificationCount);
        return "search";
    }

    @GetMapping("/getProfile")
    public String getProfile(@RequestParam("userId") int userId, Model model) {
        Object currentUser = userService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("user", currentUser);
        }
        Regular searchedUsr = regularService.getOne(userId).orElseThrow(() ->
                new UsernameNotFoundException("Can not find a user associated with this id" + userId));
        model.addAttribute("searchedUsr", searchedUsr);

        User userById = userService.getUserById(userId);
        List<Photo> photos = userById.getPhotos();
        photos.sort((photo1, photo2) ->
                photo2.getId() - photo1.getId()
        );

        String followStatue = "Follow";
        boolean isPrivate = false;
        User user = userService.getCurrentUser();
        Optional<Follow> foundFollow = followService.findFollowByFollowerAndFollowee(user, userById);
        if (foundFollow.isPresent()) {
            if (foundFollow.get().getStatus() == FollowStatus.APPROVED) {
                followStatue = "Following";
            } else if (foundFollow.get().getStatus() == FollowStatus.PENDING) {
                followStatue = "Pending";
                isPrivate = true;
            }
        } else {
            if (userById.getIsPrivate()) {
                isPrivate = true;
            }
        }

        //Finding followers and followees - When I'm saving follows to db, my idea was follower is the sender and followee is the receiver that's why I am reversing getters
        int followingCount = followService.getFollowers(FollowStatus.APPROVED, userId).size();
        int followersCount = followService.getFollowees(FollowStatus.APPROVED, userId).size();

        model.addAttribute("photos", photos);
        model.addAttribute("postCount", photos.size());
        model.addAttribute("followStatue", followStatue);
        model.addAttribute("isPrivate", isPrivate);
        model.addAttribute("followersCount", followersCount);
        model.addAttribute("followingCount", followingCount);
        model.addAttribute("newPhoto", new Photo());


        List<Notification> notificationList = notificationService.getAllNotificationsUserNotSeen();
        int notificationCount = notificationList.size();
        model.addAttribute("notificationCount", notificationCount);

        List<User> followers = new ArrayList<>();
        List<Follow> follows = searchedUsr.getUser().getFollowees();
        for (var follower : follows) {
            followers.add(follower.getFollower());
        }

        List<User> followees = new ArrayList<>();
        List<Follow> followList = searchedUsr.getUser().getFollowers();
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

        return "get-profile";
    }

    @PostMapping("/follow")
    public String followUser(@RequestParam("searchedUsrId") int searchedUsrId,
                             @RequestParam("redirectUrl") String redirectUrl) {
        User currentUser = userService.getCurrentUser();
        User searchedUsr = userService.getUserById(searchedUsrId);

        Optional<Follow> follow = followService.findFollowByFollowerAndFollowee(currentUser, searchedUsr);
        if (follow.isEmpty()) {
            Follow followRequest = new Follow();
            followService.saveFollow(followRequest, currentUser, searchedUsr);
        } else {
            followService.deleteFollow(follow.get());
        }
        return "redirect:" + redirectUrl;
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


        return "explore";
    }
}
