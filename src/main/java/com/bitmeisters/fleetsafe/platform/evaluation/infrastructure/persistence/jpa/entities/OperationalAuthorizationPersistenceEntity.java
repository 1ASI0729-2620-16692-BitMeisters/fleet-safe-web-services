package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.AuthorizationStatus;
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

@Entity
@Table(name = "operational_authorizations")
@Getter
@Setter
@NoArgsConstructor
public class OperationalAuthorizationPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "evaluation_id", nullable = false)
    private UUID evaluationId;

    @Column(name = "vehicle_id", nullable = false)
    private UUID vehicleId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthorizationStatus status;

    @Column(name = "is_override", nullable = false)
    private boolean override;

    @Column(name = "override_reason", columnDefinition = "text")
    private String overrideReason;

    @Column(name = "authorized_by")
    private UUID authorizedBy;

    @Column(name = "authorized_at", nullable = false)
    private Instant authorizedAt;

    @Column(name = "valid_until")
    private Instant validUntil;
}
