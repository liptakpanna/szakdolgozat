package com.dna.application.backend.dto;

import com.dna.application.backend.model.Alignment;
import com.dna.application.backend.model.BamUrl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlignmentDto {
    private Long id;

    private String name;

    private Alignment.Aligner aligner;

    private String description;

    private String referenceUrl;

    private Set<BamUrl> bamUrls;

    private Alignment.Visibility visibility;

    private String owner;

    private Date createdAt;

    private Date updatedAt;

    private String updatedBy;

    private List<String> userAccess;
}
