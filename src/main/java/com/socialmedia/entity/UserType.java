package com.socialmedia.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_types")
public class UserType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String typeName;

    @OneToMany(targetEntity = User.class, mappedBy = "userType", cascade = CascadeType.ALL)
    private List<User> users;
}


