package com.dna.application.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="alignments")
public class Alignment extends BaseEntityAudit {
    @Column(name = "name", nullable = false)
    private String name;

    private String description;

    private Visibility visibility;

    public enum Visibility {PUBLIC, PRIVATE, TOPSECRET};
}
