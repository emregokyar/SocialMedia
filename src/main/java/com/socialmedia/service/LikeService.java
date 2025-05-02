package com.socialmedia.service;

import com.socialmedia.entity.Like;
import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import com.socialmedia.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserService userService;

    @Autowired
    public LikeService(LikeRepository likeRepository, UserService userService) {
        this.likeRepository = likeRepository;
        this.userService = userService;
    }

    public Like saveLike(Photo photo) {
        User user = userService.getCurrentUser();
        Like like = new Like();
        like.setUser(user);
        like.setPhoto(photo);
        return likeRepository.save(like);
    }

    public boolean isUserLiked(Photo photo) {
        User currentUser = userService.getCurrentUser();
        return likeRepository.existsByPhotoAndUser(photo, currentUser);
    }

    public void deleteLike(Photo photo) {
        User currentUser = userService.getCurrentUser();
        Like like = likeRepository.findByPhotoAndUser(photo, currentUser).orElseThrow(() ->
                new RuntimeException("Can not find a like with this photo and user"));
        likeRepository.delete(like);
    }
}
