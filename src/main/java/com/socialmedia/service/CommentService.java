package com.socialmedia.service;

import com.socialmedia.entity.Comment;
import com.socialmedia.entity.Notification;
import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import com.socialmedia.repository.CommentRepository;
import com.socialmedia.util.NotificationTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserService userService, NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    public Comment getCommentById(int id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Can not find comment by id: " + id));
    }

    public void saveComment(Comment comment, Photo photo) {
        User user = userService.getCurrentUser();
        if (user != null) {
            comment.setUser(user);
        }
        comment.setPhoto(photo);
        comment.setRegistrationDate(new Date(System.currentTimeMillis()));
        Comment savedComment = commentRepository.save(comment);

        //Creating notification
        Notification notification = new Notification();
        notification.setTypedId(savedComment.getId());
        notificationService.saveNotification(notification, NotificationTypes.COMMENT, photo.getUser());
    }

    public void deleteById(int commentId) {
        Comment comment = getCommentById(commentId);

        //Deleting notification
        Optional<Notification> foundNotification = notificationService.findNotByCommentOnPhoto(comment.getPhoto().getUser(), comment);
        foundNotification.ifPresent(notificationService::deleteNotifications);

        comment.setUser(null);
        comment.setPhoto(null);
        //Deleting comment
        commentRepository.delete(comment);
    }
}
