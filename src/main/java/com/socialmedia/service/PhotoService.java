package com.socialmedia.service;

import com.socialmedia.entity.Photo;
import com.socialmedia.entity.User;
import com.socialmedia.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PhotoService {
    private final PhotoRepository photoRepository;

    @Autowired
    public PhotoService(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
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
}
