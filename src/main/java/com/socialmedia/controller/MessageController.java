package com.socialmedia.controller;

import com.socialmedia.dto.MessageDTO;
import com.socialmedia.dto.UserDTO;
import com.socialmedia.entity.*;
import com.socialmedia.service.ChatChannelService;
import com.socialmedia.service.MessageService;
import com.socialmedia.service.NotificationService;
import com.socialmedia.service.UserService;
import com.socialmedia.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/messages")
public class MessageController {
    private final UserService userService;
    private final NotificationService notificationService;
    private final ChatChannelService chatChannelService;
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MessageController(UserService userService, NotificationService notificationService, ChatChannelService chatChannelService, MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.chatChannelService = chatChannelService;
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("")
    public String getMessages(Model model) {
        Object currentUser = userService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);
            model.addAttribute("currentUsername", currentUsername);
        }
        model.addAttribute("currentUser", currentUser);

        List<Notification> notificationList = notificationService.getAllNotificationsUserNotSeen();
        int notificationCount = notificationList.size();
        model.addAttribute("notificationCount", notificationCount);

        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("newPhoto", new Photo());
        model.addAttribute("currentUserId", user.getId());

        Integer messageCount = messageService.countMessages(user);
        model.addAttribute("messageCount", messageCount);
        return "message-page";
    }

    @GetMapping("/messagedUsers")
    public ResponseEntity<List<UserDTO>> getMessagedUsers() {
        User currentUser = userService.getCurrentUser();
        var list = chatChannelService.getRegisteredChannels();
        List<User> messagedUsers = new ArrayList<>();
        list.forEach(channel -> {
            if (channel.getUser() != currentUser) messagedUsers.add(channel.getUser());
            if (channel.getRecipient() != currentUser) messagedUsers.add(channel.getRecipient());
        });

        messagedUsers.remove(currentUser);

        List<UserDTO> messagedUserDTOS = new ArrayList<>();
        messagedUsers.forEach(user -> {
            String fullName = "";
            if (user.getRegular().getFirstName() == null && user.getRegular().getLastName() == null) {
                fullName = user.getUsername();
            } else {
                fullName = user.getRegular().getFirstName() + " " + user.getRegular().getLastName();
            }
            messagedUserDTOS.add(new UserDTO(user.getId(), user.getRegular().getProfilePhotoPath(), fullName));
        });
        return ResponseEntity.ok(messagedUserDTOS);
    }

    @GetMapping("/{recipientId}")
    public ResponseEntity<List<Message>> getMessages(@PathVariable("recipientId") int recipientId) {
        List<Message> messagesBetweenUsers = messageService.getMessagesBetweenUsers(recipientId);
        messagesBetweenUsers.forEach(message -> {
            message.setHasViewed(true);
            messageService.updateMessage(message);
        });
        return ResponseEntity.ok(messagesBetweenUsers);
    }

    @MessageMapping("/send")
    public void sendMessage(@Payload MessageDTO messageDTO,
                            Principal principal) {

        Optional<User> currentUser = userService.getUserByUsername(principal.getName());
        if (currentUser.isPresent()) {
            Message message = new Message();
            message.setContent(messageDTO.getContent());
            message.setType(messageDTO.getType());
            message.setSender(currentUser.get());

            // Save and send
            Message savedMessage = messageService.save(message, messageDTO.getReceiverId());
            MessageDTO receiving_message = new MessageDTO(messageDTO.getContent(), messageDTO.getType(), messageDTO.getReceiverId(), currentUser.get().getRegular().getProfilePhotoPath(), currentUser.get().getId());
            messagingTemplate.convertAndSendToUser(
                    messageDTO.getReceiverId().toString(), "/messages", receiving_message
            );
        }
    }

    @GetMapping("/searchUser")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String input) {
        List<User> userList = userService.searchUser(input);
        User currentUser = userService.getCurrentUser();
        userList.remove(currentUser);

        List<UserDTO> userDTOS = new ArrayList<>();
        userList.forEach(user -> {
            String fullName = "";
            if (user.getRegular().getFirstName() == null && user.getRegular().getLastName() == null) {
                fullName = user.getUsername();
            } else {
                fullName = user.getRegular().getFirstName() + " " + user.getRegular().getLastName();
            }
            userDTOS.add(new UserDTO(user.getId(), user.getRegular().getProfilePhotoPath(), fullName));
        });

        return ResponseEntity.ok(userDTOS);
    }

    @PostMapping("/uploadPhoto")
    @ResponseBody
    public Map<String, String> uploadPhoto(@RequestParam("photo") MultipartFile photo,
                                           @RequestParam("receiverId") String receiverId,
                                           @RequestParam("content") String content,
                                           Principal principal) throws IOException {

        Optional<User> currentUser = userService.getUserByUsername(principal.getName());
        User recipient = userService.getUserById(Integer.valueOf(receiverId));
        if (currentUser.isPresent() && recipient != null) {
            ChatChannel channel = chatChannelService.createOrUpdateChannel(currentUser.get().getId(), recipient.getId());
            String imageName = "";
            if (!Objects.equals(photo.getOriginalFilename(), "")) {
                var fileName= StringUtils.cleanPath(Objects.requireNonNull(photo.getOriginalFilename()));
                String extension = fileName.substring(fileName.lastIndexOf("."));
                imageName = content + extension;
            }

            try {
                String uploadDir = "photos/messages/" + channel.getId();
                if (!Objects.equals(photo.getOriginalFilename(), "")) {
                    FileUploadUtil.saveFile(uploadDir, imageName, photo);
                    return Map.of("photoPath", (uploadDir + "/" + imageName)); //Return photo path for retrieving the photo
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null; // If file is empty return null
    }

}
