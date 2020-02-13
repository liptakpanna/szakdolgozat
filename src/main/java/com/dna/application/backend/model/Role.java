package com.dna.application.backend.model;

import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToMany;
import java.util.Collection;

@Entity
public class Role extends BaseEntity {
  
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
 
    private String name;
    @ManyToMany(mappedBy = "roles")
    private Collection<User> users;
}