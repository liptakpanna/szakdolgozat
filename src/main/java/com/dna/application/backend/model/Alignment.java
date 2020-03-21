package com.dna.application.backend.model;

import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="alignments")
public class Alignment extends BaseEntityAudit {
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    private Aligner aligner;

    private String referenceUrl;

    @OneToMany(mappedBy = "alignment", fetch = FetchType.EAGER)
    @Fetch(value= FetchMode.SELECT)
    private Set<BamUrl> bamUrls;

    @Column(columnDefinition = "VARCHAR(1000)", length=1000)
    private String description;

    private Visibility visibility;

    @ManyToOne
    @Fetch(value= FetchMode.SELECT)
    private User owner;

    @ManyToMany(mappedBy = "alignmentAccess", fetch = FetchType.EAGER)
    @Fetch(value= FetchMode.SELECT)
    private Set<User> userAccess = new HashSet<>();

    public enum Visibility {PUBLIC, PRIVATE, TOPSECRET};

    public enum Aligner {BOWTIE, BWA};
}
