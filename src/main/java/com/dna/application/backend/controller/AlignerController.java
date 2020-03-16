package com.dna.application.backend.controller;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.model.Alignment;
import com.dna.application.backend.model.AlignmentRequest;
import com.dna.application.backend.model.User;
import com.dna.application.backend.service.AlignmentService;
import com.dna.application.backend.service.BowtieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private AlignmentService alignmentService;

    @GetMapping("/list")
    @ResponseBody
    public List<AlignmentDto> getAlignments(Authentication authentication){
        return alignmentService.getAlignments((User)authentication.getPrincipal());
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    @ResponseBody
    public List<AlignmentDto> deleteAlignment(@RequestParam Long id, Authentication authentication) throws Exception{
        User user = (User)authentication.getPrincipal();
        return alignmentService.deleteAlignment(id, user);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    public AlignmentDto doAlignment(@ModelAttribute AlignmentRequest alignmentRequest, Authentication authentication) throws Exception {
        if(alignmentRequest.getAligner().equals(Alignment.Aligner.BOWTIE))
            return bowtieService.align(alignmentRequest, (User)authentication.getPrincipal());
        else throw new Exception("Not a valid aligner");
    }
}