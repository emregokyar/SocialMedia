package com.socialmedia.service;

import com.socialmedia.repository.RegularRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegularService {
    private final RegularRepository regularRepository;

    @Autowired
    public RegularService(RegularRepository regularRepository) {
        this.regularRepository = regularRepository;
    }
}
