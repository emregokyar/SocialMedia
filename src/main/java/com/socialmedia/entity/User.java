package com.socialmedia.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull
    @Column(unique = true, nullable = false)
    private String username;

    @NotNull
    @Column(nullable = false)
    private String password;
    private Boolean isActive;
    private Boolean isPrivate;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date registrationDate;

    private String passwordToken;

    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    @JsonBackReference
    private UserType userType;

    @OneToMany(targetEntity = Photo.class, mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Photo> photos;

    @OneToMany(targetEntity = Follow.class, mappedBy = "follower", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Follow> followers;

    @OneToMany(targetEntity = Follow.class, mappedBy = "followee", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Follow> followees;
}
