package com.dna.application.backend.service;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.model.Alignment;
import com.dna.application.backend.model.AlignmentRequest;
import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.AlignmentRepository;
import com.dna.application.backend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AlignmentService {
    @Value("${data.resource.folder}")
    private String folder;

    @Autowired
    private AlignmentRepository alignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public List<AlignmentDto> getAlignments(User user) {
        Set<Alignment> alignments = new HashSet<>(alignmentRepository.findByVisibility(Alignment.Visibility.PUBLIC));
        alignments.addAll(user.getOwnedAlignments());
        alignments.addAll(user.getAlignmentAccess());

        if (user.getRole() == User.Role.ADMIN) {
            alignments.addAll(alignmentRepository.findByVisibility(Alignment.Visibility.PRIVATE));
        }

        List<AlignmentDto> alignmentDtos = new ArrayList<>();
        for (Alignment alignment : alignments) {
            alignmentDtos.add(getAlignmentDto(alignment.getName()));
        }
        return alignmentDtos;
    }

    public AlignmentDto getAlignmentDto(String name) {
        Alignment alignment = alignmentRepository.findByName(name);

        return AlignmentDto.builder()
                .id(alignment.getId())
                .name(alignment.getName())
                .referenceUrl(alignment.getReferenceUrl())
                .bamUrl(alignment.getBamUrl())
                .description(alignment.getDescription())
                .aligner(alignment.getAligner())
                .visibility(alignment.getVisibility())
                .owner(getOwnerName(alignment))
                .createdAt(alignment.getCreatedAt())
                .updatedAt(alignment.getUpdatedAt())
                .updatedBy(alignment.getUpdatedBy())
                .userAccess(alignment.getUserAccess() != null ? alignment.getUserAccess().stream().map(User::getUsername).collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }

    public List<AlignmentDto> deleteAlignment(Long id, User user) throws Exception{
        Alignment alignment = alignmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));

        if (alignment.getOwner() != user || user.getRole() != User.Role.ADMIN)
            throw new Exception("You have no authorization to delete this object");

        String filename = alignment.getName().replaceAll("\\s+","_");
        deleteAlignmentFiles(filename);

        alignmentRepository.deleteById(id);
        alignmentRepository.flush();
        return getAlignments(user);
    }

    public AlignmentDto updateAlignment(AlignmentRequest alignmentRequest, User user) throws Exception{
        Long id = alignmentRequest.getId();
        Alignment alignment = alignmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id.toString()));

        if (alignment.getOwner() != user && user.getRole() != User.Role.ADMIN)
            throw new Exception("You have no authorization to edit this object");

        String name = alignmentRequest.getName();
        String description = alignmentRequest.getDescription();
        Alignment.Visibility visibility = alignmentRequest.getVisibility();
        List<String> usernameAccessList = alignmentRequest.getUsernameAccessList();

        if(alignmentRequest.getName() != null) alignment.setName(name);
        if(alignmentRequest.getDescription() != null) alignment.setDescription(description);
        if(alignmentRequest.getVisibility() != null) alignment.setVisibility(visibility);

        Set<User> userAccessList = new HashSet<>();
        if(usernameAccessList != null)
            for(String username: usernameAccessList) {
                User userToAdd = userRepository.findByUsername(username);
                if(userToAdd != null)
                    userAccessList.add(userToAdd);
            }
        alignment.setUserAccess(userAccessList);
        alignment.setUpdatedBy(user.getUsername());

        alignmentRepository.saveAndFlush(alignment);
        return getAlignmentDto(alignment.getName());
    }

    private void deleteAlignmentFiles(String filename) throws Exception{
        String[] args = new String[]{folder+"/delete_alignment_script", filename, folder};

        Process proc = new ProcessBuilder(args).start();
        String error = getError(proc);

        proc.waitFor();
        log.warn(error);
    }

    private String getError(Process proc) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        StringBuilder ans = new StringBuilder();
        String line;

        while((line = reader.readLine()) != null) {
            ans.append(line);
        }
        return ans.toString();
    }

    private String getOwnerName(Alignment alignment) {
        if(alignment.getOwner().getStatus() == User.Status.DELETED) {
            return "[deleted user]";
        } else {
            return alignment.getOwner().getUsername();
        }
    }
}