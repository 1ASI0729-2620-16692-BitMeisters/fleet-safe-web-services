package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.RepairStatus;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "repairs")
@Getter
@Setter
@NoArgsConstructor
public class RepairPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "incident_id", nullable = false)
    private UUID incidentId;

    @Column(name = "workshop", nullable = false, length = 200)
    private String workshop;

    @Column(name = "cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal cost;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "finished_at")
    private Instant finishedAt;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RepairStatus status;
}