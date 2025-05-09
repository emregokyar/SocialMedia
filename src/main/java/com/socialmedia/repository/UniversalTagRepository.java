package com.socialmedia.repository;

import com.socialmedia.entity.UniversalTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UniversalTagRepository extends JpaRepository<UniversalTag, Integer> {
    Optional<UniversalTag> findByTagName(String tagName);
}
