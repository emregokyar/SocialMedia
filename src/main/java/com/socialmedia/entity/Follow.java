package com.socialmedia.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.socialmedia.util.FollowStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "follows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@IdClass(FollowId.class)
public class Follow {

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdAt;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date respondedAt;

    @Enumerated(EnumType.STRING)
    private FollowStatus status;

    @Id
    @ManyToOne
    @JoinColumn(name = "follower_id", referencedColumnName = "id")
    @JsonBackReference
    private User follower;

    @Id
    @ManyToOne
    @JoinColumn(name = "followee_id", referencedColumnName = "id")
    @JsonBackReference
    private User followee;
}
