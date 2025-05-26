package com.socialmedia.repository;

import com.socialmedia.entity.ChatChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatChannelRepository extends JpaRepository<ChatChannel, Integer> {

    @Query(value = "SELECT DISTINCT cnl.*" +
            " FROM chat_channels cnl" +
            " WHERE cnl.user_id = :userId" +
            " OR cnl.recipient_id = :userId" +
            " ORDER BY cnl.updated_at DESC",
            nativeQuery = true)
    List<ChatChannel> getRegisteredChannels(@Param("userId") int userId);

    @Query(value = "SELECT DISTINCT cnl.*" +
            " FROM chat_channels cnl" +
            " WHERE (cnl.user_id = :userId AND cnl.recipient_id = :recipientId)" +
            " OR (cnl.user_id = :recipientId AND cnl.recipient_id = :userId)",
            nativeQuery = true)
    Optional<ChatChannel> getChannel(@Param("userId") int userId, @Param("recipientId") int recipientId);
}
