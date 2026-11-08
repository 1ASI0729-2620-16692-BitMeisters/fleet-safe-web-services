package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "corrective_actions")
@Getter
@Setter
@NoArgsConstructor
public class CorrectiveActionPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "incident_id", nullable = false)
    private UUID incidentId;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "performed_by", nullable = false)
    private UUID performedBy;

    @Column(name = "performed_at", nullable = false)
    private Instant performedAt;

    @Column(name = "evidence_url", length = 2048)
    private String evidenceUrl;
}