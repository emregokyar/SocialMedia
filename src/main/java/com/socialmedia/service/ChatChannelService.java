package com.socialmedia.service;

import com.socialmedia.entity.ChatChannel;
import com.socialmedia.entity.User;
import com.socialmedia.repository.ChatChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ChatChannelService {
    private final ChatChannelRepository chatChannelRepository;
    private final UserService userService;

    @Autowired
    public ChatChannelService(ChatChannelRepository chatChannelRepository, UserService userService) {
        this.chatChannelRepository = chatChannelRepository;
        this.userService = userService;
    }

    public List<ChatChannel> getRegisteredChannels() {
        User currentUser = userService.getCurrentUser();
        return chatChannelRepository.getRegisteredChannels(currentUser.getId());
    }

    public ChatChannel createOrUpdateChannel(int userId,int recipientId) {
        User currentUser = userService.getUserById(userId);
        User recipient = userService.getUserById(recipientId);

        Optional<ChatChannel> channel = chatChannelRepository.getChannel(currentUser.getId(), recipientId);
        if (channel.isPresent()) {
            channel.get().setUpdatedAt(new Date(System.currentTimeMillis()));
            return chatChannelRepository.save(channel.get());
        }

        int newId = Integer.parseInt(currentUser.getId() + "" + recipient.getId());
        channel = Optional.of(new ChatChannel());
        channel.get().setId(newId);
        channel.get().setUser(currentUser);
        channel.get().setRecipient(recipient);
        channel.get().setUpdatedAt(new Date(System.currentTimeMillis()));
        return chatChannelRepository.save(channel.get());
    }
}
