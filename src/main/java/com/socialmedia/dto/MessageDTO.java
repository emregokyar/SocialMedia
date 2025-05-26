package com.socialmedia.dto;

import com.socialmedia.util.MessageType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class MessageDTO {
    private String content;
    private MessageType type;
    private Integer receiverId;
    private String picturePath;
    private Integer senderId;

    public MessageDTO(String content, MessageType type, Integer receiverId) {
        this.content = content;
        this.type = type;
        this.receiverId = receiverId;
    }

    public MessageDTO(String content, MessageType type, Integer receiverId, String picturePath) {
        this.content = content;
        this.type = type;
        this.receiverId = receiverId;
        this.picturePath = picturePath;
    }
}

