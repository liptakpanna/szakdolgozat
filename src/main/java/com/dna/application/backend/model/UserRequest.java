package com.dna.application.backend.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private Long id;

    private String username;

    private String email;

    private String password;

    private User.Role role;

    private User.Status status;
}