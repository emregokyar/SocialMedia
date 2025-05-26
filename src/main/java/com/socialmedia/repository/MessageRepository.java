package com.socialmedia.repository;

import com.socialmedia.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query(value = "SELECT DISTINCT msg.*" +
            " FROM messages msg" +
            " WHERE msg.sender_id = :userId" +
            " OR msg.receiver_id = :userId" +
            " ORDER BY msg.created_at DESC",
            nativeQuery = true)
    List<Message> getAllMessages(@Param("userId") int userId);

    @Query(value = "SELECT DISTINCT msg.*" +
            " FROM messages msg" +
            " WHERE (msg.sender_id = :userId AND msg.receiver_id = :recipientId)" +
            " OR (msg.sender_id = :recipientId AND msg.receiver_id = :userId)" +
            " ORDER BY msg.created_at",
            nativeQuery = true)
    List<Message> getMessagesBetween(@Param("userId") int userId, @Param("recipientId") int recipientId);

    @Query(value = "SELECT COUNT(DISTINCT msg.sender_id)" +
            " FROM messages msg" +
            " WHERE msg.receiver_id = :userId" +
            " AND msg.has_viewed = FALSE",
            nativeQuery = true)
    Integer countReceivedMessages(@Param("userId") int userId);
}
