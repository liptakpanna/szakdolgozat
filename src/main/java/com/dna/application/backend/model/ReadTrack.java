package com.dna.application.backend.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Data
@NoArgsConstructor
public class ReadTrack {
    private String name;

    private boolean isPaired;

    private MultipartFile read1;

    private MultipartFile read2;

    private String validCount;

    private String mismatch;

    private List<String> penalties;
}
