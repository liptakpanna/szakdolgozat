package com.dna.application.backend.repository;

import com.dna.application.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    List<User> findByUsername(List<String> username);

    @Query("select t.username from User t where t.status=ENABLED")
    List<String> findUsernames();

    boolean existsByUsername(String username);

    @Query("select t.email from User t where t.role=ADMIN")
    List<String> findAdminEmails();
}
