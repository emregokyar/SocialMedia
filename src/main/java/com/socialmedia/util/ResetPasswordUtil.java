package com.socialmedia.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class ResetPasswordUtil {
    private final JavaMailSender mailSender;

    @Autowired
    public ResetPasswordUtil(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String createRandomToken() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789";
        char[] chars = alphabet.toCharArray();

        StringBuilder sb = new StringBuilder(30);
        for (int i = 0; i < 30; i++) {
            int index = (int) (chars.length * Math.random());
            char picked = chars[index];
            sb.append(picked);
        }
        return sb.toString();
    }

    public String getSiteUrl(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        System.out.println(siteURL); //Delete later
        return siteURL.replace(request.getServletPath(), "");
    }

    public void sendEmail(String email, String link) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("emregokyar1940@gmial.com", "U-ME Support");
        helper.setTo(email);

        String subject = "Here link to reset your password";
        String content = "<p> Hello <p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);
    }
}
