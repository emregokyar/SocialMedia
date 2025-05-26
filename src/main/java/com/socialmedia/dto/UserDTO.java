package com.socialmedia.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {
    private Integer id;
    private String photoPath;
    private String fullName;

    public UserDTO(Integer id, String photoPath) {
        this.id = id;
        this.photoPath = photoPath;
    }
}
