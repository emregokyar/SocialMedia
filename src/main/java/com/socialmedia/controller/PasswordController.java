package com.socialmedia.controller;

import com.socialmedia.entity.User;
import com.socialmedia.service.UserService;
import com.socialmedia.util.ResetPasswordUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Controller
public class PasswordController {
    private final UserService userService;
    private final ResetPasswordUtil resetPasswordUtil;

    @Autowired
    public PasswordController(UserService userService, ResetPasswordUtil resetPasswordUtil) {
        this.userService = userService;
        this.resetPasswordUtil = resetPasswordUtil;
    }

    @PostMapping("/sendResetRequest")
    public String sendResetRequest(HttpServletRequest request,
                                   @RequestParam("email") String email,
                                   Model model) {

        Optional<User> userByEmail = userService.getUserByEmail(email);
        if (userByEmail.isEmpty()) {
            model.addAttribute("error", "You haven't signed up yet, please register an account.");
            model.addAttribute("newUser", new User());
            return "index";
        }

        String token = resetPasswordUtil.createRandomToken();
        try {
            userService.updatePasswordToken(token, email);
            String resetPasswordLink = resetPasswordUtil.getSiteUrl(request) + "/getResetPage?token=" + token;
            resetPasswordUtil.sendEmail(email, resetPasswordLink);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Something went wrong, can not send an email " + e);
        }

        return "redirect:/";
    }

    @GetMapping("/getResetPage")
    public String getResetPasswordPage(@RequestParam("token") String token, Model model) {
        User user = userService.getUserByResetToken(token);
        if (user == null) return "redirect:/";

        model.addAttribute("token", token);
        return "reset-password-from";
    }

    @PostMapping("/updatePassword")
    public String updatePassword(@RequestParam("token") String token,
                                 @RequestParam("password") String newPassword) {
        User user = userService.getUserByResetToken(token);
        if (user == null) return "redirect:/";
        userService.updatePassword(user, newPassword);
        return "redirect:/";
    }
}
