package com.socialmedia.service;

import com.socialmedia.entity.Follow;
import com.socialmedia.entity.User;
import com.socialmedia.repository.FollowRepository;
import com.socialmedia.util.FollowStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    public boolean isUserFollowing(User follower, User followee) {
        return followRepository.existsByFollowerAndFollowee(follower, followee);
    }

    public Optional<Follow> findFollowByFollowerAndFollowee(User follower, User followee) {
        return followRepository.findByFollowerAndFollowee(follower, followee);
    }


    public void deleteFollow(Follow follow) {
        followRepository.delete(follow);
    }

    public List<User> getFollowers(FollowStatus statue, int userId) {

        return followRepository.getFollowers(userId, statue.toString());
    }

    public List<User> getFollowees(FollowStatus statue, int userId) {

        return followRepository.getFollowees(userId, statue.toString());
    }

}
