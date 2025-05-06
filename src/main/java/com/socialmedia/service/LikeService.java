package com.socialmedia.service;

import com.socialmedia.entity.Like;
import com.socialmedia.entity.Notification;
import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import com.socialmedia.repository.LikeRepository;
import com.socialmedia.util.NotificationTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public LikeService(LikeRepository likeRepository, UserService userService, NotificationService notificationService) {
        this.likeRepository = likeRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public void saveLike(Photo photo) {
        User user = userService.getCurrentUser();
        Like like = new Like();
        like.setUser(user);
        like.setPhoto(photo);
        likeRepository.save(like);

        //Sending notification
        Notification notification = new Notification();
        notification.setTypedId(photo.getId());
        notificationService.saveNotification(notification, NotificationTypes.LIKE, photo.getUser());
    }

    public boolean isUserLiked(Photo photo) {
        User currentUser = userService.getCurrentUser();
        return likeRepository.existsByPhotoAndUser(photo, currentUser);
    }

    public void deleteLike(Photo photo) {
        User currentUser = userService.getCurrentUser();
        Like like = likeRepository.findByPhotoAndUser(photo, currentUser).orElseThrow(() ->
                new RuntimeException("Can not find a like with this photo and user"));

        //Retrieving back the notification
        Optional<Notification> foundNotification = notificationService.findNotByLikedPhoto(photo);
        foundNotification.ifPresent(notificationService::deleteNotifications);

        likeRepository.delete(like);
    }
}
