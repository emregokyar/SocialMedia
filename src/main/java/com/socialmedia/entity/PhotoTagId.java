package com.socialmedia.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PhotoTagId implements Serializable {

    private Integer photoId;
    private Integer universalTagId;
}