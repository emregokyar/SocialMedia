package com.socialmedia.service;

import com.socialmedia.entity.Comment;
import com.socialmedia.entity.Notification;
import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import com.socialmedia.repository.NotificationRepository;
import com.socialmedia.util.NotificationTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserService userService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    public Notification saveNotification(Notification notification) {
        notification.setCreatedAt(new Date(System.currentTimeMillis()));
        notification.setRead(false);
        User currentUser = userService.getCurrentUser();
        notification.setSender(currentUser);
        return notificationRepository.save(notification);
    }

    public Notification saveNotification(Notification notification, NotificationTypes notificationType, User receiver) {
        User sender = userService.getCurrentUser();

        notification.setTypes(notificationType);
        notification.setCreatedAt(new Date(System.currentTimeMillis()));
        notification.setRead(false);
        notification.setSender(sender);
        notification.setReceiver(receiver);

        if (notificationType.equals(NotificationTypes.LIKE)) {
            notification.setMessage("User liked your photo");
        }
        switch (notificationType) {
            case LIKE -> notification.setMessage(sender.getUsername() + " liked your photo.");
            case FOLLOW -> {
                if (receiver.getIsPrivate()) {
                    notification.setMessage(sender.getUsername() + " wants to follow you.");
                } else {
                    notification.setMessage(sender.getUsername() + " started following you.");
                }
            }
            case COMMENT -> notification.setMessage(sender.getUsername() + " commented on your post.");
            case MESSAGE -> notification.setMessage(sender.getUsername() + " send a message.");
            default -> notification.setMessage("You have a notification.");
        }
        return notificationRepository.save(notification);
    }

    public void deleteNotifications(Notification notification) {
        notificationRepository.delete(notification);
    }

    public Optional<Notification> findNotByLikedPhoto(Photo photo) {
        User sender = userService.getCurrentUser();
        return notificationRepository.findNotByLikedPhoto(sender.getId(), photo.getUser().getId(), photo.getId());
    }

    public Optional<Notification> findNotByCommentOnPhoto(User receiver, Comment comment) {
        User sender = userService.getCurrentUser();
        return notificationRepository.findNotByCommentOnPhoto(sender.getId(), receiver.getId(), comment.getId());
    }

    public Optional<Notification> findNotByFollowStatus(User receiver) {
        User sender = userService.getCurrentUser();
        return notificationRepository.findNotByFollowStatus(sender.getId(), receiver.getId());
    }

    public List<Notification> getAllNotifications() {
        User user = userService.getCurrentUser();
        return notificationRepository.getNotifications(user.getId());
    }
}
