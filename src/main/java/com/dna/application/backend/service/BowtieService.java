package com.dna.application.backend.service;

import com.dna.application.backend.model.Alignment;
import com.dna.application.backend.model.AlignmentRequest;
import com.dna.application.backend.model.AlignmentResponse;
import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.AlignmentRepository;
import com.dna.application.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
public class BowtieService extends BaseAligner {
    @Autowired
    private AlignmentRepository alignmentRepository;

    @Autowired
    private UserRepository userRepository;

    public AlignmentResponse align(AlignmentRequest alignmentRequest) throws Exception{
        String username = alignmentRequest.getUsername();
        String name = alignmentRequest.getName();
        String description = alignmentRequest.getDescription();
        String indexRoute = alignmentRequest.getIndexRoute(); //"/bowtie/indexes/e_coli"
        String dnaRoute = alignmentRequest.getDnaRoute(); //"/bowtie/reads/e_coli_1000.fq"
        Alignment.Visibility visibility = alignmentRequest.getVisibility();
        List<String> usernameAccessList = alignmentRequest.getUsernameAccessList();

        String[] args = new String[]{"bowtie", resourceFolder + indexRoute, resourceFolder + dnaRoute};

        Process proc = new ProcessBuilder(args).start();
        String ans = getInput(proc);
        String error = getError(proc);

        proc.waitFor();

        AlignmentResponse alignmentResponse = new AlignmentResponse();
        alignmentResponse.setName(name);
        alignmentResponse.setAligner(Alignment.Aligner.BOWTIE);

        alignmentResponse.setResult(ans + error);

        Alignment alignment = Alignment.builder()
                .aligner(Alignment.Aligner.BOWTIE)
                .name(name)
                .description(description)
                .owner(username)
                .route("null")
                .visibility(visibility)
                .userAccess(new HashSet<User>(userRepository.findByUsername(usernameAccessList)))
                .build();

        alignmentRepository.saveAndFlush(alignment);

        return alignmentResponse;
    }
}
