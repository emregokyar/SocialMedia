package com.socialmedia.entity;

import lombok.*;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FollowId implements Serializable {
    private Integer follower;
    private Integer followee;
}
//Created for using composite key in db
