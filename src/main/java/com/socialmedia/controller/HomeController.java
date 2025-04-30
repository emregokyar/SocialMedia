package com.socialmedia.controller;

import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
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
import java.util.Objects;

@Controller
public class HomeController {
    private final UserService userService;
    private final PhotoService photoService;

    @Autowired
    public HomeController(UserService userService, PhotoService photoService) {
        this.userService = userService;
        this.photoService = photoService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        Object currentUser = userService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);
        }
        model.addAttribute("user", currentUser);
        model.addAttribute("photo", new Photo());
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
