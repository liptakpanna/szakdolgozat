package com.dna.application.backend.service;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.model.Alignment;
import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.AlignmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class AlignerService {
    @Autowired
    private AlignmentRepository alignmentRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @Transactional
    public List<AlignmentDto> getAlignments(User user) {
        User.Role role = user.getRole();

        Set<Alignment> alignments = new HashSet<>(alignmentRepository.findByVisibility(Alignment.Visibility.PUBLIC));
        alignments.addAll(user.getOwnedAlignments());

        if (role == User.Role.ADMIN) {
            alignments.addAll(alignmentRepository.findByVisibility(Alignment.Visibility.PRIVATE));
        }

        alignments.addAll(user.getAlignmentAccess());

        List<AlignmentDto> alignmentDtos = new ArrayList<>();
        for (Alignment alignment : alignments) {
            alignmentDtos.add(AlignmentDto.builder()
                    .id(alignment.getId())
                    .name(alignment.getName())
                    .route(alignment.getRoute())
                    .description(alignment.getDescription())
                    .aligner(alignment.getAligner())
                    .visibility(alignment.getVisibility())
                    .owner(alignment.getOwner().getUsername())
                    .build()
            );
        }

        return alignmentDtos;
    }


}
