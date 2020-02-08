package com.dna.application.backend.model;

import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@Entity
@Table(name="Users")
public class User extends BaseEntityAudit{
    @Column(name = "user_name", unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
}
