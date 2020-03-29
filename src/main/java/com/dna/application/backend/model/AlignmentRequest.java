package com.dna.application.backend.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
