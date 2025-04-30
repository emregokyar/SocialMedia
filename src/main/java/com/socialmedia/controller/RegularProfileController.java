package com.socialmedia.controller;

import com.socialmedia.entity.Photo;
import com.socialmedia.entity.Regular;
import com.socialmedia.entity.User;
import com.socialmedia.service.RegularService;
import com.socialmedia.service.UserService;
import com.socialmedia.util.FileUploadUtil;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
public class RegularProfileController {
    private final RegularService regularService;
    private final UserService userService;

    @Autowired
    public RegularProfileController(RegularService regularService, UserService userService) {
        this.regularService = regularService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String getUserProfile(Model model) {
        Object currentUser = userService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);
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
