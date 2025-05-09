package com.socialmedia.service;

import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import com.socialmedia.repository.PhotoRepository;
import com.socialmedia.util.FollowStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PhotoService {
    private final PhotoRepository photoRepository;
    private final UserService userService;

    @Autowired
    public PhotoService(PhotoRepository photoRepository, UserService userService) {
        this.photoRepository = photoRepository;
        this.userService = userService;
    }

    public Photo savePhoto(Photo photo, User currentUser, String extension) {
        photo.setRegistrationDate(new Date(System.currentTimeMillis()));
        photo.setUser(currentUser);
        photo.setExtension(extension);
        return photoRepository.save(photo);
    }

    public Photo findById(int photoId) {

        return photoRepository.findById(photoId).orElseThrow(() ->
                new RuntimeException("Con not find a photo associated with this id " + photoId));
    }

    public Photo updatePhoto(Photo photo) {
        return photoRepository.save(photo);
    }

    public List<Photo> getFollowingPosts() {
        User user = userService.getCurrentUser();
        return photoRepository.getFollowingPosts(user.getId(), FollowStatus.APPROVED.toString());
    }

    public List<Photo> getMostLikedPosts(int howManyDays) {
        User user = userService.getCurrentUser();
        return photoRepository.getMostLikedPhotos(user.getId(), howManyDays);
    }

    public List<Photo> getLikedPosts() {
        User user = userService.getCurrentUser();
        return photoRepository.getLikedPosts(user.getId(), FollowStatus.APPROVED.toString());
    }

    public List<Photo> getPhotosSearchedTagAndLocation(String input) {
        int userId = userService.getCurrentUser().getId();
        return photoRepository.findByUniversalTag(input, userId);
    }

    public List<Photo> getPhotosUserFollowingDependsOnLocationAndTag(String input) {
        int userId = userService.getCurrentUser().getId();
        return photoRepository.searchFromFollowingPost(userId, FollowStatus.APPROVED.toString(), input);
    }
}





