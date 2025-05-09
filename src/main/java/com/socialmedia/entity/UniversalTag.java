package com.socialmedia.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "universal_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UniversalTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private String tagName;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdAt;

    @OneToMany(targetEntity = PhotoTag.class, mappedBy = "universalTag", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<PhotoTag> photoTags;
}
