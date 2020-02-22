package com.dna.application.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateRequest {
    private Long id;

    private String username;

    private String email;

    private String password;

    private User.Role role;
}