package com.socialmedia.repository;

import com.socialmedia.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @Query(value = "SELECT DISTINCT ntf.id, ntf.types, ntf.created_at, ntf.message, ntf.is_read, ntf.sender_id, ntf.receiver_id, ntf.typed_id" +
            " FROM notifications ntf" +
            " JOIN photos pht ON pht.user_id= ntf.receiver_id" +
            " WHERE ntf.sender_id= :senderId" +
            " AND ntf.types= 'LIKE'" +
            " AND ntf.receiver_id= :receiverId" +
            " AND ntf.typed_id= :typedId", nativeQuery = true)
    Optional<Notification> findNotByLikedPhoto(int senderId, int receiverId, int typedId);

    @Query(value = "SELECT DISTINCT ntf.id, ntf.types, ntf.created_at, ntf.message, ntf.is_read, ntf.sender_id, ntf.receiver_id, ntf.typed_id" +
            " FROM notifications ntf" +
            " JOIN comments cmt ON cmt.user_id= ntf.sender_id" +
            " WHERE ntf.sender_id= :senderId" +
            " AND ntf.types= 'COMMENT'" +
            " AND ntf.receiver_id= :receiverId" +
            " AND ntf.typed_id= :typedId", nativeQuery = true)
    Optional<Notification> findNotByCommentOnPhoto(int senderId, int receiverId, int typedId);

    @Query(value = "SELECT DISTINCT ntf.id, ntf.types, ntf.created_at, ntf.message, ntf.is_read, ntf.sender_id, ntf.receiver_id, ntf.typed_id" +
            " FROM notifications ntf" +
            " JOIN follows flw ON flw.follower_id= ntf.sender_id" +
            " WHERE ntf.sender_id= :senderId" +
            " AND ntf.types= 'FOLLOW'" +
            " AND ntf.receiver_id= :receiverId" +
            " AND flw.follower_id= :senderId" +
            " AND flw.followee_id= :receiverId", nativeQuery = true)
    Optional<Notification> findNotByFollowStatus(int senderId, int receiverId);

    @Query(value = "SELECT * FROM notifications ntf" +
            " WHERE ntf.receiver_id= :userId" +
            " ORDER BY ntf.created_at DESC", nativeQuery = true)
    List<Notification> getNotifications(int userId);

    @Query(value = "SELECT * FROM notifications ntf" +
            " WHERE ntf.receiver_id= :userId" +
            " AND ntf.is_read= FALSE" +
            " ORDER BY ntf.created_at DESC", nativeQuery = true)
    List<Notification> getAllNotificationsUserNotSeen(int userId);
}
