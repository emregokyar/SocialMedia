package com.socialmedia.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.socialmedia.util.NotificationTypes;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdAt;

    @Enumerated(EnumType.STRING)
    private NotificationTypes types;

    private String message;
    private boolean isRead;
    private Integer typedId;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    @JsonBackReference
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    @JsonBackReference
    private User receiver;
}
