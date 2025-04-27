package com.socialmedia.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

//Redirecting users depends on the success login
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        boolean hasRegularRole= authentication.getAuthorities().stream().anyMatch(role->role.getAuthority().equals("Regular"));
        boolean hasAdminRole= authentication.getAuthorities().stream().anyMatch(role->role.getAuthority().equals("Admin"));

        if (hasAdminRole){
            response.sendRedirect("/dashboard");
        }else if (hasRegularRole){
            response.sendRedirect("/home");
        }
    }
}
