package com.dna.application.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UsernameListResponse {
    private List<String> usernames;
}
