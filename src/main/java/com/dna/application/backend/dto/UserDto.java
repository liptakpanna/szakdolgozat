package com.dna.application.backend.dto;

import com.dna.application.backend.model.User;
import lombok.*;

import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;

    private String username;

    private String email;

    private User.Role role;

    private String updatedBy;

    private Date createdAt;

    private Date updatedAt;

    private String createdBy;

}
