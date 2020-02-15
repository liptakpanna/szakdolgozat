package com.dna.application.backend.controller;

import com.dna.application.backend.model.User;
import com.dna.application.backend.service.BowtieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_RESEARCHER')")
@RequestMapping("/api/align")
public class AlignerController {

    @Autowired
    private BowtieService bowtieService;

    @GetMapping("/bowtie")
    @ResponseBody
    public String getBowtie(Authentication authentication) throws Exception{
        User user = (User)authentication.getPrincipal();
        return bowtieService.align(user);
    }
}
