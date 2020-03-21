package com.dna.application.backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name="bam_urls")
public class BamUrl extends BaseEntity {
    @ManyToOne
    @Fetch(value= FetchMode.SELECT)
    private Alignment alignment;

    private String name;

    private String url;

    public BamUrl(String n, String u) {
        name = n;
        url = u;
    }
}
