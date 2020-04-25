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

    public UserRequest(Long _id, String _username, String _email, String _password, User.Role _role) {
        id = _id;
        username = _username;
        email = _email;
        password = _password;
        role = _role;
    }
}