package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.shared.domain.model.valueobjects.*;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentOrigin;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentSeverity;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "incidents")
public class IncidentPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Column(name = "incident_type_id", nullable = false)
    private UUID incidentTypeId;

    @Column(name = "inspection_id")
    private UUID inspectionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin", nullable = false)
    private IncidentOrigin origin;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private IncidentSeverity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IncidentStatus status;

    @Column(name = "reported_by", nullable = false)
    private UUID reportedBy;

    @Column(name = "reported_at", nullable = false)
    private Instant reportedAt;

    @Column(name = "resolution_type")
    private String resolutionType;

    @Column(name = "resolved_at")
    private Instant resolvedAt;
}