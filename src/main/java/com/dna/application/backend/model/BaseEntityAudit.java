package com.dna.application.backend.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class BaseEntityAudit extends BaseEntity implements Serializable {

    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    public void setCreationDate() {
        this.createdAt = new Date();
    }

    @PreUpdate
    public void setChangeDate() {
        this.updatedAt = new Date();
    }
}
