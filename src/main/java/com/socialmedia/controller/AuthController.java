package com.socialmedia.controller;

import com.socialmedia.entity.User;
import com.socialmedia.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("newUser", new User());
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

        if (userByEmail.isPresent()) {
            model.addAttribute("error", "This email is already registered, please try another mail address.");
        } else model.addAttribute("error", "This username is already taken, please try another username.");

        model.addAttribute("newUser", new User());
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("error", "Not a valid username or password!");
        model.addAttribute("newUser", new User());
        return "index";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(httpServletRequest, httpServletResponse, authentication);
        }
        return "redirect:/";
    }
}