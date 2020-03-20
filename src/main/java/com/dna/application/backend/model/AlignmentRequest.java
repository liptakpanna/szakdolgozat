package com.dna.application.backend.model;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AlignmentRequest {
    private Long id;

    private Alignment.Aligner aligner;

    private String name;

    private String description;

    private MultipartFile referenceDna;

    private List<ReadTrack> readsForDna;

    private Alignment.Visibility visibility;

    private List<String> usernameAccessList;

    private Long referenceId;
}
