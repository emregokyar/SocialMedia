package com.socialmedia.controller;

import com.socialmedia.entity.Follow;
import com.socialmedia.entity.Photo;
import com.socialmedia.entity.Regular;
import com.socialmedia.entity.User;
import com.socialmedia.service.FollowService;
import com.socialmedia.service.RegularService;
import com.socialmedia.service.UserService;
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

import java.util.List;

@Controller
public class SearchController {
    private final UserService userService;
    private final RegularService regularService;
    private final FollowService followService;

    @Autowired
    public SearchController(UserService userService, RegularService regularService, FollowService followService) {
        this.userService = userService;
        this.regularService = regularService;
        this.followService = followService;
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

        List<Regular> searchedUsers = regularService.searchUsers(input);
        model.addAttribute("searchedUsers", searchedUsers);
        model.addAttribute("oldInput", input);

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
        model.addAttribute("photos", photos);
        model.addAttribute("postCount", photos.size());

        return "get-profile";
    }

    @PostMapping("/follow")
    public String followUser(@RequestParam("searchedUsrId") int searchedUsrId,
                             @RequestParam("redirectUrl") String redirectUrl) {
        User currentUser = userService.getCurrentUser();
        User searchedUsr = userService.getUserById(searchedUsrId);
        Follow followRequest = new Follow();

        followService.saveFollow(followRequest, currentUser, searchedUsr);
        return "redirect:" + redirectUrl;
    }
}
