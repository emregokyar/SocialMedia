package com.socialmedia.repository;

import com.socialmedia.entity.Like;
import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    boolean existsByPhotoAndUser(Photo photo, User user);

    Optional<Like> findByPhotoAndUser(Photo photo, User user);
}
