package com.socialmedia.service;

import com.socialmedia.entity.Regular;
import com.socialmedia.entity.User;
import com.socialmedia.repository.RegularRepository;
import com.socialmedia.repository.UserRepository;
import com.socialmedia.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final RegularRepository regularRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserTypeRepository userTypeRepository, RegularRepository regularRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userTypeRepository = userTypeRepository;
        this.regularRepository = regularRepository;
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
}
