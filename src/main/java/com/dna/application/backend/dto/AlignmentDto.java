package com.dna.application.backend.dto;

import com.dna.application.backend.model.Alignment;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlignmentDto {
    private Long id;

    private String name;

    private Alignment.Aligner aligner;

    private String owner;

    private String description;

    private String route;

    private Alignment.Visibility visibility;
}
