package com.dna.application.backend.controller;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.exception.CommandNotFoundException;
import com.dna.application.backend.exception.EntityNameAlreadyExistsException;
import com.dna.application.backend.exception.WrongFileTypeException;
import com.dna.application.backend.model.AlignmentRequest;
import com.dna.application.backend.model.ReadTrack;
import com.dna.application.backend.model.ReferenceExample;
import com.dna.application.backend.model.User;
import com.dna.application.backend.service.AlignmentService;
import com.dna.application.backend.service.BowtieService;
import com.dna.application.backend.service.BwaService;
import com.dna.application.backend.service.SnapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/api/align")
public class AlignerController {

    @Autowired
    private BowtieService bowtieService;

    @Autowired
    private BwaService bwaService;

    @Autowired
    private SnapService snapService;

    @Autowired
    private AlignmentService alignmentService;

    @Value("${default.error.message}")
    private String errorMessage;

    @Value("${max.request.filesize}")
    private int maxFileSize;

    @GetMapping("/list")
    public List<AlignmentDto> getAlignments(Authentication authentication) {
        try {
            return alignmentService.getAlignments((User) authentication.getPrincipal());
        } catch (Exception e) {
            log.debug(e.getMessage());
            return null;
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    public ResponseEntity<Boolean> deleteAlignment(@PathVariable Long id, Authentication authentication) {
        User user = (User)authentication.getPrincipal();
        try {
            return ResponseEntity.ok(alignmentService.deleteAlignment(id, user));
        } catch (Exception e) {
            log.debug(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    public ResponseEntity<?> doAlignment(@ModelAttribute AlignmentRequest alignmentRequest, Authentication authentication) {
        if(isFileSizeLimitExceeded(alignmentRequest)) {
            Map.Entry<String,String> value=new AbstractMap.SimpleEntry<>("message", "Maximum upload size ("+ maxFileSize + "MB) exceeded.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(value);
        }
        User user = (User)authentication.getPrincipal();
        try {
            switch(alignmentRequest.getAligner()) {
                case BOWTIE:
                    return ResponseEntity.ok(bowtieService.align(alignmentRequest, user));
                case SNAP:
                    return ResponseEntity.ok(snapService.align(alignmentRequest, user));
                case BWA:
                    return ResponseEntity.ok(bwaService.align(alignmentRequest, user));
                default:
                    throw new Exception("Not a valid aligner");
            }
        } catch (EntityNameAlreadyExistsException e) {
            Map.Entry<String,String> value=new AbstractMap.SimpleEntry<>("message", "Alignment name already exists.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(value);
        } catch (WrongFileTypeException e) {
            Map.Entry<String, String> value = new AbstractMap.SimpleEntry<>("message", "Wrong file type");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(value);
        } catch (CommandNotFoundException e) {
            Map.Entry<String, String> value = new AbstractMap.SimpleEntry<>("message",
                    "A command was not found, the server does not have everything installed to work properly.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(value);
        } catch (Exception e) {
            log.debug(e.getMessage());
            Map.Entry<String, String> value = new AbstractMap.SimpleEntry<>("message", errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(value);
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    public ResponseEntity<?> updateAlignment(@RequestBody AlignmentRequest alignmentRequest, Authentication authentication)  {
        User user = (User)authentication.getPrincipal();
        try {
            return ResponseEntity.ok(alignmentService.updateAlignment(alignmentRequest, user));
        } catch (EntityNameAlreadyExistsException e){
            Map.Entry<String,String> value=new AbstractMap.SimpleEntry<>("message", "Alignment name already exists.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(value);
        } catch (Exception e) {
            Map.Entry<String,String> value=new AbstractMap.SimpleEntry<>("message", errorMessage);
            log.debug(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(value);
        }
    }

    @GetMapping("/referencelist")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    public List<ReferenceExample> getReferences() {
        return alignmentService.getReferences();
    }

    private boolean isFileSizeLimitExceeded(AlignmentRequest request) {
        double size = 0;
        if(request.getReferenceDna() != null) size += request.getReadsForDna().size();
        for(ReadTrack track : request.getReadsForDna()) {
            size += track.getRead1().getSize();
            if(track.getRead2() != null) size += track.getRead2().getSize();
        }
        return size/1000000 > maxFileSize;
    }
}