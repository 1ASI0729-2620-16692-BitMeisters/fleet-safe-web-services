package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "incident_follow_ups")
@Getter
@Setter
@NoArgsConstructor
public class IncidentFollowUpPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "incident_id", nullable = false)
    private UUID incidentId;

    @Column(name = "note", nullable = false, columnDefinition = "text")
    private String note;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;
}