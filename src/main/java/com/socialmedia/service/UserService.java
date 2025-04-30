package com.socialmedia.service;

import com.socialmedia.entity.Admin;
import com.socialmedia.entity.Regular;
import com.socialmedia.entity.User;
import com.socialmedia.repository.AdminRepository;
import com.socialmedia.repository.RegularRepository;
import com.socialmedia.repository.UserRepository;
import com.socialmedia.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final RegularRepository regularRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserTypeRepository userTypeRepository, RegularRepository regularRepository, AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.regularRepository = regularRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User addNewUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);
        user.setRegistrationDate(new Date(System.currentTimeMillis()));
        user.setUserType(userTypeRepository.getReferenceById(1));
        User savedUser = userRepository.save(user);

        Regular regular = new Regular();
        regular.setUser(user);
        regularRepository.save(regular);
        return savedUser;
    }

    public Object getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            User currentUser = userRepository.findByUsername(username).orElseThrow(() ->
                    new UsernameNotFoundException("Can not found a user associated with this name"));

            int userId = currentUser.getId();
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Regular"))) {
                return regularRepository.findById(userId).orElse(new Regular());
            } else return adminRepository.findById(userId).orElse(new Admin());
        }
        return null;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            return userRepository.findByUsername(username).orElseThrow(() ->
                    new UsernameNotFoundException("Can not found a user associated with this name"));
        }
        return null;
    }

    //Add user delete later on settings area!!
    public void delete(User currentUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            userRepository.delete(currentUser);
        }
    }
}
