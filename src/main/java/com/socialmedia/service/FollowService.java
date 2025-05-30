package com.socialmedia.service;

import com.socialmedia.entity.Follow;
import com.socialmedia.entity.Notification;
import com.socialmedia.entity.User;
import com.socialmedia.repository.FollowRepository;
import com.socialmedia.util.FollowStatus;
import com.socialmedia.util.NotificationTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FollowService {
    private final FollowRepository followRepository;
    private final NotificationService notificationService;

    @Autowired
    public FollowService(FollowRepository followRepository, NotificationService notificationService) {
        this.followRepository = followRepository;
        this.notificationService = notificationService;
    }

    public void saveFollow(Follow follow, User sender, User receiver) {
        if (receiver.getIsPrivate()) {
            follow.setStatus(FollowStatus.PENDING);
        } else {
            follow.setStatus(FollowStatus.APPROVED);
        }
        follow.setCreatedAt(new Date(System.currentTimeMillis()));
        follow.setFollower(sender);
        follow.setFollowee(receiver);
        Follow savedFollow = followRepository.save(follow);

        //Send notifications
        Notification notification = new Notification();
        notification.setTypedId(savedFollow.getFollower().getId());//Saving id is the follow sender id
        notificationService.saveNotification(notification, NotificationTypes.FOLLOW, savedFollow.getFollowee());
    }

    public boolean isUserFollowing(User follower, User followee) {
        return followRepository.existsByFollowerAndFollowee(follower, followee);
    }

    public Optional<Follow> findFollowByFollowerAndFollowee(User follower, User followee) {
        return followRepository.findByFollowerAndFollowee(follower, followee);
    }


    public void deleteFollow(Follow follow) {
        //Deleting follow and follow notification
        Optional<Notification> deletedNot = notificationService.findNotByFollowStatus(follow.getFollowee());
        deletedNot.ifPresent(notificationService::deleteNotifications);

        followRepository.delete(follow);
    }

    public List<User> getFollowers(FollowStatus statue, int userId) {
        return followRepository.getFollowers(userId, statue.toString());
    }

    public List<User> getFollowees(FollowStatus statue, int userId) {
        return followRepository.getFollowees(userId, statue.toString());
    }

    public void plainSaveFollow(Follow follow) {
        followRepository.save(follow);
    }

    public void deletePlainFollow(Follow follow) {
        followRepository.delete(follow);
    }
}
