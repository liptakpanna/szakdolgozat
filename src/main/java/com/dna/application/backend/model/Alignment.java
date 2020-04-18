package com.dna.application.backend.model;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name="alignments")
public class Alignment extends BaseEntityAudit {
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    private Aligner aligner;

    private String referenceUrl;

    @Column(columnDefinition = "VARCHAR(1000)", length=1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @ManyToOne
    private User owner;

    @ManyToMany(mappedBy = "alignmentAccess")
    private Set<User> userAccess = new HashSet<>();

    @OneToMany(orphanRemoval=true)
    @JoinColumn(name = "alignment_id")
    private Set<BamUrl> bamUrls;

    public enum Visibility {PUBLIC, PRIVATE, PRIVATE_GROUP};

    public enum Aligner {BOWTIE, BWA, SNAP};
}
