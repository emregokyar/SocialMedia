package com.socialmedia.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.socialmedia.dto.UserDTO;
import com.socialmedia.util.MessageType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    private boolean hasViewed;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    @JsonBackReference
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    @JsonBackReference
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "channel_id", referencedColumnName = "id")
    @JsonBackReference
    private ChatChannel channel;

    public UserDTO getSenderDTO() {
        String profilePhoto = sender.getRegular().getProfilePhoto();
        if (profilePhoto != null) {
            profilePhoto = "/photos/users/" + sender.getId() + "/profile_photos/" + profilePhoto;
        } else {
            profilePhoto = "/assets/person-fill.svg";
        }
        return new UserDTO(sender.getId(), profilePhoto);
    }

    public UserDTO getReceiverDTO() {
        String profilePhoto = receiver.getRegular().getProfilePhoto();
        if (profilePhoto != null) {
            profilePhoto = "/photos/users/" + receiver.getId() + "/profile_photos/" + profilePhoto;
        } else {
            profilePhoto = "/assets/person-fill.svg";
        }
        return new UserDTO(receiver.getId(), profilePhoto);
    }
}
