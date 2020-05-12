package com.dna.application.backend.repository;

import com.dna.application.backend.model.BamUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BamUrlRepository extends JpaRepository<BamUrl, Long> {
}
