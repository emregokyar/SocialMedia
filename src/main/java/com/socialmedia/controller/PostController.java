package com.socialmedia.controller;

import com.socialmedia.entity.Comment;
import com.socialmedia.entity.Photo;
import com.socialmedia.entity.Regular;
import com.socialmedia.entity.User;
import com.socialmedia.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@Controller
public class PostController {
    private final PhotoService photoService;
    private final RegularService regularService;
    private final UserService userService;
    private final CommentService commentService;
    private final LikeService likeService;


    @Autowired
    public PostController(PhotoService photoService, RegularService regularService, UserService userService, CommentService commentService, LikeService likeService) {
        this.photoService = photoService;
        this.regularService = regularService;
        this.userService = userService;
        this.commentService = commentService;
        this.likeService = likeService;
    }

    @GetMapping("/showPost")
    public String showPost(@RequestParam("photoId") int photoId, Model model) {
        User currentUser = userService.getCurrentUser();
        Regular currentUserProfile= regularService.getOne(currentUser.getId()).orElseThrow(()->
                new UsernameNotFoundException("Can not found a user with this id"));
        Photo photo = photoService.findById(photoId);
        if (photo == null) {
            throw new RuntimeException("Can not find a photo associated with this id: " + photoId);
        }
        Regular searchedUsr = regularService.getOne(photo.getUser().getId()).orElseThrow(() ->
                new UsernameNotFoundException("Can not find a user associated with this user id"));
        User profile = photo.getUser();
        int postCount = profile.getPhotos().size();
        List<Comment> comments = photo.getComments();
        model.addAttribute("postCount", postCount);
        model.addAttribute("photo", photo);
        model.addAttribute("searchedUsr", searchedUsr);
        model.addAttribute("currentUsername", currentUser.getUsername());
        model.addAttribute("currentUserProfile", currentUserProfile);

        model.addAttribute("username", photo.getUser().getUsername());

        model.addAttribute("comments", comments);
        model.addAttribute("comment", new Comment());

        //Checking if user already liked the photo
        boolean hasUserLiked = likeService.isUserLiked(photo);
        model.addAttribute("hasLiked", hasUserLiked);
        return "post";
    }

    @PostMapping("/commentOnPost")
    public String commentOnPost(@RequestParam("photoId") int photoId, @Valid Comment comment, Model model) {
        Photo photo = photoService.findById(photoId);
        if (comment != null && !comment.getUserComment().isEmpty()) {
            commentService.saveComment(comment, photo);
        }

        return "redirect:/showPost?photoId=" + photoId;
    }

    @PostMapping("/deleteUserComment")
    public String deleteUserComment(@RequestParam("commentId") int commentId) {
        Comment comment = commentService.getCommentById(commentId);
        Photo photo = comment.getPhoto();
        User user = userService.getCurrentUser();
        //Check this if statement - basically for checking who is authorized to delete
        if (Objects.equals(comment.getUser().getUsername(), user.getUsername())) {
            commentService.deleteById(commentId);
        }

        return "redirect:/showPost?photoId=" + photo.getId();
    }


    @PostMapping("/likePhoto")
    public String likePhoto(@RequestParam("photoId") int photoId) {
        Photo photo = photoService.findById(photoId);
        boolean liked = likeService.isUserLiked(photo);
        if (!liked) {
            likeService.saveLike(photo);
        } else {
            likeService.deleteLike(photo);
        }
        return "redirect:/showPost?photoId=" + photoId;
    }

    @PostMapping("/updatePostInfo")
    public String updatePostInfo(@ModelAttribute Photo photo) {
        User currentUser = userService.getCurrentUser();
        Photo checkedPhoto = photoService.findById(photo.getId());
        //Checking who is authorized to update post
        if (Objects.equals(currentUser.getId(), checkedPhoto.getUser().getId())) {
            checkedPhoto.setCaption(photo.getCaption());
            checkedPhoto.setLocation(photo.getLocation());
            photoService.updatePhoto(checkedPhoto);
        }
        return "redirect:/showPost?photoId=" + photo.getId();
    }
}