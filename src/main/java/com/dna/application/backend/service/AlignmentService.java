package com.dna.application.backend.service;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.exception.EntityNameAlreadyExistsException;
import com.dna.application.backend.model.*;
import com.dna.application.backend.repository.AlignmentRepository;
import com.dna.application.backend.repository.BamUrlRepository;
import com.dna.application.backend.repository.ReferenceRepository;
import com.dna.application.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AlignmentService extends BaseCommandRunner {
    @Value("${data.resource.folder}")
    private String folder;

    @Value("${data.resource.url}")
    public String resourceUrl;

    @Autowired
    private AlignmentRepository alignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReferenceRepository referenceRepository;

    @Autowired
    private BamUrlRepository bamUrlRepository;

    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public List<AlignmentDto> getAlignments(User user) throws Exception {
        Set<Alignment> alignments = new HashSet<>(alignmentRepository.findByVisibility(Alignment.Visibility.PUBLIC));
        alignments.addAll(user.getOwnedAlignments());
        alignments.addAll(user.getAlignmentAccess());

        if (user.getRole() == User.Role.ADMIN) {
            alignments.addAll(alignmentRepository.findByVisibility(Alignment.Visibility.PRIVATE));
        }

        List<AlignmentDto> alignmentDtos = new ArrayList<>();
        if(alignments.size() > 0)
            for (Alignment alignment : alignments) {
                alignmentDtos.add(getAlignmentDto(alignment.getName()));
            }
        return alignmentDtos;
    }

    public AlignmentDto getAlignmentDto(String name) throws Exception {
        Alignment alignment = alignmentRepository.findByName(name);
        if(alignment != null)
            return AlignmentDto.builder()
                    .id(alignment.getId())
                    .name(alignment.getName())
                    .referenceUrl(alignment.getReferenceUrl())
                    .bamUrls(alignment.getBamUrls())
                    .description(alignment.getDescription())
                    .aligner(alignment.getAligner())
                    .visibility(alignment.getVisibility())
                    .owner(getOwnerName(alignment))
                    .createdAt(alignment.getCreatedAt())
                    .updatedAt(alignment.getUpdatedAt())
                    .updatedBy(alignment.getUpdatedBy())
                    .userAccess(alignment.getUserAccess() != null ? alignment.getUserAccess().stream().map(User::getUsername).collect(Collectors.toList()) : new ArrayList<>())
                    .build();
        else throw new Exception("Alignment with name:" + name + " not found");
    }

    public Boolean deleteAlignment(Long id, User user) throws Exception{
        Alignment alignment = alignmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));

        if (alignment.getOwner() != user && user.getRole() != User.Role.ADMIN)
            throw new Exception("You have no authorization to delete this object");

        String filename = alignment.getName().replaceAll("\\s+","_");
        deleteAlignmentFiles(filename, alignment.getBamUrls().size(), alignment.getReferenceUrl().contains("/examples/"));

        alignmentRepository.deleteById(id);
        alignmentRepository.flush();

        return !alignmentRepository.existsById(id);
    }

    public AlignmentDto updateAlignment(AlignmentRequest alignmentRequest, User user) throws Exception{
        Long id = alignmentRequest.getId();
        Alignment alignment = alignmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));

        if (!alignment.getOwner().getUsername().equals(user.getUsername()) && user.getRole() != User.Role.ADMIN)
            throw new Exception("You have no authorization to edit this object");

        String name = alignmentRequest.getName();
        String description = alignmentRequest.getDescription();
        Alignment.Visibility visibility = alignmentRequest.getVisibility();
        List<String> usernameAccessList = alignmentRequest.getUsernameAccessList();
        log.warn("Alignment request: {}", usernameAccessList);

        if(name != null) {
            if (!name.equals(alignment.getName()) && alignmentRepository.existsByName(name)) throw new EntityNameAlreadyExistsException();
            String newRefUrl = renameAlignmentFiles(alignment.getName().replaceAll("\\s+","_"), name.replaceAll("\\s+","_"), alignment.getBamUrls(), alignment.getReferenceUrl().contains("/examples/"));
            if(newRefUrl != null) alignment.setReferenceUrl(newRefUrl);
            alignment.setName(name);
        }
        if(description != null) alignment.setDescription(description);
        if(visibility != null) alignment.setVisibility(visibility);

        if(usernameAccessList != null)
            for(String username: usernameAccessList) {
                User userToAdd = userRepository.findByUsername(username);
                if(userToAdd != null) {
                    userToAdd.getAlignmentAccess().add(alignment);
                }
            }
        alignment.setUpdatedBy(user.getUsername());

        alignmentRepository.saveAndFlush(alignment);
        return getAlignmentDto(alignment.getName());
    }

    public List<ReferenceExample> getReferences(){
        return referenceRepository.findAll();
    }

    private String renameAlignmentFiles(String oldFilename, String newFilename, Set<BamUrl> bams, boolean isExample) throws Exception {
        int i = 1;
        for(BamUrl bam: bams) {
            runCommand(new String[]{"mv", folder+"bams/"+oldFilename+i+".bam", folder+"bams/"+newFilename+i+".bam"});
            runCommand(new String[]{"mv", folder+"bams/"+oldFilename+i+".bam.bai", folder+"bams/"+newFilename+i+".bam.bai"});
            bam.setUrl(resourceUrl+"/bams/"+newFilename+i+".bam");
            i++;
        }
        bamUrlRepository.saveAll(bams);

        if(!isExample) {
            runCommand(new String[]{"mv", folder+"references/"+oldFilename+".fna", folder+"references/"+newFilename+".fna"});
            runCommand(new String[]{"mv", folder+"references/"+oldFilename+".fna.fai", folder+"references/"+newFilename+".fna.fai"});
            return resourceUrl+"/references/"+newFilename+".fna";
        }
        return null;
    }

    private void deleteAlignmentFiles(String filename, int readSize, boolean isExample) throws Exception{
        runCommand(new String[]{folder+"/delete_alignment_script", filename, folder, String.valueOf(readSize), String.valueOf(isExample)});
    }

    private String getOwnerName(Alignment alignment) {
        if(alignment.getOwner().getStatus() == User.Status.DELETED) {
            return "[deleted user]";
        } else {
            return alignment.getOwner().getUsername();
        }
    }
}
