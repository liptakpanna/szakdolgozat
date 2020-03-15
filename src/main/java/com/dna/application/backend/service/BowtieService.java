package com.dna.application.backend.service;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.model.Alignment;
import com.dna.application.backend.model.AlignmentRequest;
import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.AlignmentRepository;
import com.dna.application.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class BowtieService extends BaseAligner {
    @Autowired
    private AlignmentRepository alignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlignmentService alignmentService;

    //TODO nev utkozes

    public AlignmentDto align(AlignmentRequest alignmentRequest, User user) throws Exception{
        String name = alignmentRequest.getName();
        List<String> usernameAccessList = alignmentRequest.getUsernameAccessList();
        String filename = name.replaceAll("\\s+","_");

        saveFile(alignmentRequest.getReferenceDna(), folder+"references/"+filename + ".fna");
        saveFile(alignmentRequest.getReadsForDna(), folder+"reads/"+filename + ".fastq");
        runScript(folder+"/bowtie_script", filename, folder);

        Set<User> userAccess = new HashSet<>();

        if( usernameAccessList != null && !usernameAccessList.isEmpty()) {
            userAccess.addAll(userRepository.findByUsername(usernameAccessList));
        }

        Alignment alignment = Alignment.builder()
                .aligner(Alignment.Aligner.BOWTIE)
                .name(filename)
                .description(alignmentRequest.getDescription())
                .owner(user)
                .referenceUrl(resourceUrl+"/references/"+filename+".fna")
                .bamUrl(resourceUrl+"/bams/"+filename+".bam")
                .visibility(alignmentRequest.getVisibility())
              //  .userAccess(userAccess)
                .build();

        alignmentRepository.saveAndFlush(alignment);

        return alignmentService.getAlignment(name);
    }

}
