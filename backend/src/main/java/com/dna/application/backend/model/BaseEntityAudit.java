package com.dna.application.backend.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class BaseEntityAudit extends BaseEntity implements Serializable {

    protected String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date updatedAt;

    @PrePersist
    public void setCreationDate() {
        this.createdAt = new Date();
    }

    @PreUpdate
    public void setChangeDate() {
        this.updatedAt = new Date();
    }
}
