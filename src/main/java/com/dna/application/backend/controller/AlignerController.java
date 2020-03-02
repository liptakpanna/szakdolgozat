package com.dna.application.backend.controller;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.model.Alignment;
import com.dna.application.backend.model.AlignmentRequest;
import com.dna.application.backend.model.AlignmentResponse;
import com.dna.application.backend.model.User;
import com.dna.application.backend.service.AlignerService;
import com.dna.application.backend.service.BowtieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/align")
public class AlignerController {

    @Autowired
    private BowtieService bowtieService;

    @Autowired
    private AlignerService alignerService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
    @ResponseBody
    public AlignmentResponse getAlignment(@RequestBody AlignmentRequest alignmentRequest, Authentication authentication ) throws Exception{
        if(alignmentRequest.getAligner().equals(Alignment.Aligner.BOWTIE))
            return bowtieService.align(alignmentRequest, (User)authentication.getPrincipal());
        else throw new Exception("Not a valid aligner");
    }

    @GetMapping("/list")
    @ResponseBody
    public List<AlignmentDto> getAlignments(Authentication authentication){
        return alignerService.getAlignments((User)authentication.getPrincipal());
    }

   //@PostMapping("/delete")
    //@ResponseBody
    //public List<UserDto> deleteAlignment(@RequestParam Long id, Authentication authentication) throws Exception{
      //  User user = (User)authentication.getPrincipal();
    //}
}