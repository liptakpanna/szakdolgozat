package com.dna.application.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlignmentResponse {
    private Alignment.Aligner aligner;

    private String name;

    private String result;
}
