package com.socialmedia.service;

import com.socialmedia.entity.Comment;
import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import com.socialmedia.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PhotoService photoService;
    private final UserService userService;

    @Autowired
    public CommentService(CommentRepository commentRepository, PhotoService photoService, UserService userService) {
        this.commentRepository = commentRepository;
        this.photoService = photoService;
        this.userService = userService;
    }

    public Comment getCommentById(int id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Can not find comment by id: " + id));
    }

    public Comment saveComment(Comment comment, Photo photo) {
        User user = userService.getCurrentUser();
        if (user != null) {
            comment.setUser(user);
        }
        comment.setPhoto(photo);
        comment.setRegistrationDate(new Date(System.currentTimeMillis()));
        return commentRepository.save(comment);
    }

    public void deleteById(int commentId) {
        Comment comment = getCommentById(commentId);
        comment.setUser(null);
        comment.setPhoto(null);
        commentRepository.delete(comment);
    }
}
