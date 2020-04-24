package com.dna.application.backend.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class UserRequest {
    private Long id;

    private String username;

    private String email;

    private String password;

    private User.Role role;

    private User.Status status;

    public UserRequest(Long i, String n) {
        id = i;
        username = n;
    }

    public UserRequest(Long i, String n, String e, String p, User.Role r) {
        id = i;
        username = n;
        email = e;
        password = p;
        role = r;
    }
}