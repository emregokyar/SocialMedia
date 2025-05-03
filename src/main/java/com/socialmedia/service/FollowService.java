package com.socialmedia.service;

import com.socialmedia.entity.Follow;
import com.socialmedia.entity.User;
import com.socialmedia.repository.FollowRepository;
import com.socialmedia.util.FollowStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FollowService {
    private final FollowRepository followRepository;

    public FollowService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public Follow saveFollow(Follow follow, User sender, User receiver) {
        if (receiver.getIsPrivate()) {
            follow.setStatus(FollowStatus.PENDING);
        } else {
            follow.setStatus(FollowStatus.APPROVED);
        }
        follow.setCreatedAt(new Date(System.currentTimeMillis()));
        follow.setFollower(sender);
        follow.setFollowee(receiver);
        return followRepository.save(follow);
    }
}
