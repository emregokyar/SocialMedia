package com.socialmedia.repository;

import com.socialmedia.entity.Photo;
import com.socialmedia.entity.PhotoTag;
import com.socialmedia.entity.UniversalTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoTagRepository extends JpaRepository<PhotoTag, Integer> {
    boolean existsByPhotoAndUniversalTag(Photo photo, UniversalTag universalTag);
}
