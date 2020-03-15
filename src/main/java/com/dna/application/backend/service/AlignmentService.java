package com.dna.application.backend.service;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.model.Alignment;
import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.AlignmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class AlignmentService {
    @Autowired
    private AlignmentRepository alignmentRepository;

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
            alignmentDtos.add(AlignmentDto.builder()
                    .id(alignment.getId())
                    .name(alignment.getName())
                    .referenceUrl(alignment.getReferenceUrl())
                    .bamUrl(alignment.getBamUrl())
                    .description(alignment.getDescription())
                    .aligner(alignment.getAligner())
                    .visibility(alignment.getVisibility())
                    .owner(alignment.getOwner().getUsername())
                    .build()
            );
        }
        return alignmentDtos;
    }

    public AlignmentDto getAlignment(String name) {
        Alignment alignment = alignmentRepository.findByName(name);

        return AlignmentDto.builder()
                .id(alignment.getId())
                .name(alignment.getName())
                .referenceUrl(alignment.getReferenceUrl())
                .bamUrl(alignment.getBamUrl())
                .description(alignment.getDescription())
                .aligner(alignment.getAligner())
                .visibility(alignment.getVisibility())
                .owner(alignment.getOwner().getUsername())
                .build();
    }

}
