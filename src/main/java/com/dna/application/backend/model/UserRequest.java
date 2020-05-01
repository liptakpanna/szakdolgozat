package com.dna.application.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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