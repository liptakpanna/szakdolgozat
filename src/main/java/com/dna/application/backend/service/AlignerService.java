package com.dna.application.backend.service;

import com.dna.application.backend.dto.AlignmentDto;
import com.dna.application.backend.model.Alignment;
import com.dna.application.backend.model.User;
import com.dna.application.backend.repository.AlignmentRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AlignerService {
    @Autowired
    private AlignmentRepository alignmentRepository;

    private ModelMapper modelMapper = new ModelMapper();

    public List<AlignmentDto> getAlignments(User user) {
        User.Role role = user.getRole();

        Set<Alignment> alignments = new HashSet<>(alignmentRepository.findByVisibility(Alignment.Visibility.PUBLIC));
        alignments.addAll(alignmentRepository.findByOwner(user.getUsername()));

        if (role == User.Role.ADMIN) {
            alignments.addAll(alignmentRepository.findByVisibility(Alignment.Visibility.PRIVATE));
        }

        alignments.addAll(user.getAlignmentAccess());

        Type listType = new TypeToken<List<AlignmentDto>>() {}.getType();
        return modelMapper.map(alignments, listType);
    }


}
