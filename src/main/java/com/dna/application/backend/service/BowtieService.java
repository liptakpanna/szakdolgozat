package com.dna.application.backend.service;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.model.Alignment;
import com.dna.application.backend.model.AlignmentRequest;
import com.dna.application.backend.model.ReferenceExample;
import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.AlignmentRepository;
import com.dna.application.backend.repository.ReferenceRepository;
import com.dna.application.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
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
    private ReferenceRepository referenceRepository;

    @Autowired
    private AlignmentService alignmentService;

    public AlignmentDto align(AlignmentRequest alignmentRequest, User user) throws Exception{
        String name = alignmentRequest.getName();
        if(alignmentRepository.findByName(name) != null ) throw new Exception("Name already in use");

        List<String> usernameAccessList = alignmentRequest.getUsernameAccessList();
        String filename = name.replaceAll("\\s+","_");
        Set<MultipartFile> reads = alignmentRequest.getReadsForDna();

        Long referenceId = alignmentRequest.getReferenceId();
        ReferenceExample reference = null;
        if(referenceId != null){
            reference = referenceRepository.findById(referenceId).orElseThrow(() -> new EntityNotFoundException(referenceId.toString()));
            saveFilesForAligner(null, reads, folder, filename);
            runScript(folder+"/bowtie_script", filename, folder, reads.size(), reference.getFilename());
        }
        else {
            saveFilesForAligner(alignmentRequest.getReferenceDna(), reads, folder, filename);
            runScript(folder+"/bowtie_script", filename, folder, reads.size());
        }

        Alignment alignment = Alignment.builder()
                .aligner(Alignment.Aligner.BOWTIE)
                .name(filename)
                .description(alignmentRequest.getDescription())
                .owner(user)
                .referenceUrl(reference==null ? resourceUrl+"/references/"+filename+".fna" : resourceUrl+"/examples/"+reference.getFilename()+".fna" )
                .bamUrls(getBamUrls(resourceUrl, filename, reads.size()))
                .visibility(alignmentRequest.getVisibility())
                .userAccess(getUserAccessSet(usernameAccessList, userRepository))
                .build();

        alignmentRepository.saveAndFlush(alignment);

        return alignmentService.getAlignmentDto(name);
    }
}
