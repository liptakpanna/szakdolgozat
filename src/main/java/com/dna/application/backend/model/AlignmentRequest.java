package com.dna.application.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public AlignmentRequest(Long _id, String _name, String _description, Alignment.Visibility _visibility) {
        id = _id;
        name = _name;
        description = _description;
        visibility = _visibility;
    }
}
