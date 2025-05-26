package com.socialmedia.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "admins")
public class Admin {
    @Id
    private Integer userId;
    private String firstName;
    private String lastName;
    private String profilePhoto;

    @OneToOne
    @JoinColumn(name = "userId")
    @MapsId
    @JsonBackReference //?
    private User user;

    @Transactional
    public String getProfilePhotoPath() {
        if (profilePhoto == null) return null;
        return "/photos/admins/" + userId + "/profile_photos/" + profilePhoto;
    }
}
