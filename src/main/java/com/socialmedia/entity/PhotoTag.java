package com.socialmedia.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "photo_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PhotoTag {

    @EmbeddedId
    private PhotoTagId id;

    @ManyToOne
    @JoinColumn(name = "photo_id", referencedColumnName = "id")
    @MapsId("photoId")
    @JsonBackReference
    private Photo photo;

    @ManyToOne
    @JoinColumn(name = "universal_tag_id", referencedColumnName = "id")
    @JsonBackReference
    @MapsId("universalTagId")
    private UniversalTag universalTag;
}
