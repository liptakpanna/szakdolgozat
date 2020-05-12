package com.dna.application.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReadTrack {
    private String name;

    private boolean isPaired;

    private MultipartFile read1;

    private MultipartFile read2;

    private String validCount;

    private String mismatch;

    private List<String> penalties;

    private String maxHits;

    private String maxDist;
}
