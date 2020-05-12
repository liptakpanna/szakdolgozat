package com.dna.application.backend.repository;

import com.dna.application.backend.model.ReferenceExample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceRepository extends JpaRepository<ReferenceExample, Long> {
}
