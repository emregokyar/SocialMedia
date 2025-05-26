package com.socialmedia.service;

import com.socialmedia.entity.ChatChannel;
import com.socialmedia.entity.Message;
import com.socialmedia.entity.User;
import com.socialmedia.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChatChannelService chatChannelService;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserService userService, ChatChannelService chatChannelService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.chatChannelService = chatChannelService;
    }

    public List<Message> getAllMessages() {
        User currentUser = userService.getCurrentUser();
        return messageRepository.getAllMessages(currentUser.getId());
    }

    public List<Message> getMessagesBetweenUsers(int recipientId) {
        User currentUser = userService.getCurrentUser();
        return messageRepository.getMessagesBetween(currentUser.getId(), recipientId);
    }

    public Message save(Message message, int recipientId) {
        message.setReceiver(userService.getUserById(recipientId));

        message.setHasViewed(false);
        message.setCreatedAt(new Date(System.currentTimeMillis()));
        ChatChannel savedChannel = chatChannelService.createOrUpdateChannel(message.getSender().getId(), message.getReceiver().getId());
        message.setChannel(savedChannel);
        return messageRepository.save(message);
    }

    public void updateMessage(Message message) {
        messageRepository.save(message);
    }

    public Integer countMessages(User user) {
        return messageRepository.countReceivedMessages(user.getId());
    }
}
