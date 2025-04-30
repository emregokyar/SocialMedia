package com.socialmedia.service;

import com.socialmedia.entity.Regular;
import com.socialmedia.repository.RegularRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegularService {
    private final RegularRepository regularRepository;

    @Autowired
    public RegularService(RegularRepository regularRepository) {
        this.regularRepository = regularRepository;
    }

    public Optional<Regular> getOne(Integer id) {
        return regularRepository.findById(id);
    }

    public Regular saveRegularUser(Regular regularUser) {
        return regularRepository.save(regularUser);
    }
}
