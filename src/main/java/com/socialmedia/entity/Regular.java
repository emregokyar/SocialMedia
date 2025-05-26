package com.socialmedia.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "regulars")
public class Regular {
    @Id
    private Integer userId;
    private String firstName;
    private String lastName;
    private String bio;
    private String profilePhoto;
    private LocalDate birthdate;

    @OneToOne
    @JoinColumn(name = "userId")
    @JsonBackReference
    @MapsId
    private User user;

    @Transactional
    public String getProfilePhotoPath() {
        if (profilePhoto == null) return null;
        return "/photos/users/" + userId + "/profile_photos/" + profilePhoto;
    }
}
