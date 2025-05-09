package com.socialmedia.service;

import com.socialmedia.entity.UniversalTag;
import com.socialmedia.repository.UniversalTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UniversalTagService {
    private final UniversalTagRepository universalTagRepository;

    @Autowired
    public UniversalTagService(UniversalTagRepository universalTagRepository) {
        this.universalTagRepository = universalTagRepository;
    }

    public Optional<UniversalTag> findByTagName(String tagName) {
        return universalTagRepository.findByTagName(tagName);
    }

    public UniversalTag saveTag(UniversalTag universalTag) {
        universalTag.setCreatedAt(new Date(System.currentTimeMillis()));
        return universalTagRepository.save(universalTag);
    }
}
