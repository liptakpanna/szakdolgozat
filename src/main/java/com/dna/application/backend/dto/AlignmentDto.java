package com.dna.application.backend.dto;

import com.dna.application.backend.model.Alignment;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlignmentDto {
    private Long id;

    private String name;

    private Alignment.Aligner aligner;

    private String description;

    private String referenceUrl;

    private String bamUrl;

    private Alignment.Visibility visibility;

    private String owner;

    private Date createdAt;

    private Date updatedAt;

    private String updatedBy;

    private List<String> userAccess;
}
