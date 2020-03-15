package com.dna.application.backend.repository;

import com.dna.application.backend.model.Alignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlignmentRepository extends JpaRepository<Alignment, Long> {
    List<Alignment> findByVisibility(Alignment.Visibility visibility);

    Alignment findByName(String name);
}
