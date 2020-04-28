package com.dna.application.backend.model;

import com.dna.application.backend.dto.AlignmentDto;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
    private Set<BamUrl> bamUrls = new HashSet<>();

    public enum Visibility {PUBLIC, PRIVATE, PRIVATE_GROUP};

    public enum Aligner {BOWTIE, BWA, SNAP};

    public Alignment(Long _id, String _name, String _referenceUrl, Set<BamUrl> _bamUrls,
                     String _description, Aligner _aligner, Visibility _visibility, User _owner,
                     Date _createdAt, Date _updatedAt, String _updatedBy, Set<User> _userAccess) {
        id = _id;
        name = _name;
        referenceUrl = _referenceUrl;
        bamUrls = _bamUrls;
        description = _description;
        aligner = _aligner;
        visibility = _visibility;
        owner = _owner;
        createdAt = _createdAt;
        updatedAt = _updatedAt;
        updatedBy = _updatedBy;
        userAccess = _userAccess;
    }
}
