package com.dna.application.backend.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Set;

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="alignments")
public class Alignment extends BaseEntityAudit {
    @Column(name = "name", nullable = false)
    private String name;

    private Aligner aligner;

    private String owner;

    private String route;

    private String description;

    private Visibility visibility;

    @ManyToMany(mappedBy = "alignmentAccess")
    Set<User> userAccess;

    public enum Visibility {PUBLIC, PRIVATE, TOPSECRET};

    public enum Aligner {BOWTIE};
}
