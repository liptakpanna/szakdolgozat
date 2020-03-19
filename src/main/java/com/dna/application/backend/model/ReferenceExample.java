package com.dna.application.backend.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="reference_dnas")
public class ReferenceExample extends BaseEntity{
    private String name;
    private String description;

    @Column(unique = true, nullable = false)
    private String filename;
}