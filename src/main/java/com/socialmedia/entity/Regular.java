package com.socialmedia.entity;

import jakarta.persistence.*;
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
    private String profilePhoto;
    private LocalDate birthdate;

    @OneToOne
    @JoinColumn(name = "userId")
    @MapsId
    private User user;
}
