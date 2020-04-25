package com.dna.application.backend.controller;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.exception.CommandNotFoundException;
import com.dna.application.backend.exception.EntityNameAlreadyExistsException;
import com.dna.application.backend.exception.WrongFileTypeException;
import com.dna.application.backend.model.AlignmentRequest;
import com.dna.application.backend.model.ReferenceExample;
import com.dna.application.backend.model.User;
import com.dna.application.backend.service.AlignmentService;
import com.dna.application.backend.service.BowtieService;
import com.dna.application.backend.service.BwaService;
import com.dna.application.backend.service.SnapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/list")
    public List<AlignmentDto> getAlignments(Authentication authentication) throws Exception{
        return alignmentService.getAlignments((User)authentication.getPrincipal());
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    public ResponseEntity<Boolean> deleteAlignment(@PathVariable Long id, Authentication authentication) throws Exception{
        User user = (User)authentication.getPrincipal();
        if (alignmentService.deleteAlignment(id, user))
            return ResponseEntity.ok(true);
        else
            throw new Exception("Delete was not successful");
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    public ResponseEntity<AlignmentDto> doAlignment(@ModelAttribute AlignmentRequest alignmentRequest, Authentication authentication)
            throws Exception {
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
            throw new Exception("Alignment name already exists.");
        } catch (WrongFileTypeException e) {
            throw new Exception("Wrong file type.");
        } catch (CommandNotFoundException e) {
            throw new Exception("A command was not found, the server does not have everything installed to work properly.");
        } catch (Exception e) {
            throw new Exception(errorMessage);
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    public ResponseEntity<AlignmentDto> updateAlignment(@RequestBody AlignmentRequest alignmentRequest, Authentication authentication) throws Exception {
        User user = (User)authentication.getPrincipal();
        try {
            return ResponseEntity.ok(alignmentService.updateAlignment(alignmentRequest, user));
        } catch (EntityNameAlreadyExistsException e){
            throw new Exception("Alignment name already exists.");
        } catch (Exception e) {
            throw new Exception(errorMessage);
        }
    }

    @GetMapping("/referencelist")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    public List<ReferenceExample> getReferences() {
        return alignmentService.getReferences();
    }
}