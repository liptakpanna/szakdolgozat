package com.dna.application.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AlignmentRequest {
    private Alignment.Aligner aligner;

    private String name;

    private String indexRoute;

    private String dnaRoute;

    private String description;

    private Alignment.Visibility visibility;

    private List<String> usernameAccessList;
}
