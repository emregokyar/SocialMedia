package com.socialmedia.controller;

import com.socialmedia.entity.User;
import com.socialmedia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class HomeController {
    private final UserService userService;

    @Autowired
    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("user", new User());

        return "index";
    }

    @PostMapping("/register")
    public String registerNewUser(Model model, @Valid User user) {
        Optional<User> userByEmail = userService.getUserByEmail(user.getEmail());
        Optional<User> userByUsername = userService.getUserByUsername(user.getUsername());

        if (userByEmail.isEmpty() && userByUsername.isEmpty()) {
            userService.addNewUser(user);
            return "redirect:/";
        }

        if (userByUsername.isPresent()) {
            model.addAttribute("error", "This username is already taken, please try another username");
        }
        if (userByEmail.isPresent()) {
            model.addAttribute("error", "This email is already registered, please enter another mail");
        }

        model.addAttribute("user", new User());
        return "index";
    }
}