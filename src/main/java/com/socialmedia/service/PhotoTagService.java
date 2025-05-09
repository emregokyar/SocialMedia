package com.socialmedia.service;

import com.socialmedia.entity.Photo;
import com.socialmedia.entity.PhotoTag;
import com.socialmedia.entity.UniversalTag;
import com.socialmedia.repository.PhotoTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhotoTagService {
    private final PhotoTagRepository photoTagRepository;

    @Autowired
    public PhotoTagService(PhotoTagRepository photoTagRepository) {
        this.photoTagRepository = photoTagRepository;
    }

    public boolean isExists(Photo photo, UniversalTag universalTag) {
        return photoTagRepository.existsByPhotoAndUniversalTag(photo, universalTag);
    }

    public void savePhotoTag(PhotoTag photoTag) {
        photoTagRepository.save(photoTag);
    }

    public void deletePhotoTag(PhotoTag photoTag) {
        photoTagRepository.delete(photoTag);
    }
}
