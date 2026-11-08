package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.valueobjects.Timestamps;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.LiftBlockCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.events.InspectionEvaluatedEvent;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.AuthorizationStatus;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class OperationalAuthorization extends AbstractDomainAggregateRoot<OperationalAuthorization> {

    private UUID id;
    private UUID evaluationId;
    private VehicleId vehicleId;
    private AuthorizationStatus status;
    private boolean override;
    private String overrideReason;
    private UUID authorizedBy;
    private Instant authorizedAt;
    private Instant validUntil;
    private Instant createdAt;
    private Instant updatedAt;

    private OperationalAuthorization() {
    }

    public OperationalAuthorization(Evaluation evaluation, VehicleId vehicleId) {
        this.evaluationId = evaluation.getId();
        this.vehicleId = vehicleId;
        this.status = AuthorizationStatus.fromImpact(evaluation.resolveMostRestrictiveImpact());
        this.override = false;
        this.authorizedBy = evaluation.getEvaluatedBy();
        this.authorizedAt = Timestamps.now();
        registerDomainEvent(new InspectionEvaluatedEvent(evaluation.getInspectionId(), vehicleId, status));
    }

    public OperationalAuthorization(OperationalAuthorization blocked, LiftBlockCommand command) {
        if (blocked.status != AuthorizationStatus.NOT_ENABLED) {
            throw new IllegalArgumentException("evaluation.authorization.error.not-blocked");
        }
        this.evaluationId = blocked.evaluationId;
        this.vehicleId = blocked.vehicleId;
        this.status = AuthorizationStatus.ENABLED;
        this.override = true;
        this.overrideReason = command.reason();
        this.authorizedBy = command.authorizedBy();
        this.authorizedAt = Timestamps.now();
    }

    public static OperationalAuthorization restore(UUID id, UUID evaluationId, VehicleId vehicleId, AuthorizationStatus status,
                                                   boolean override, String overrideReason, UUID authorizedBy,
                                                   Instant authorizedAt, Instant validUntil,
                                                   Instant createdAt, Instant updatedAt) {
        var authorization = new OperationalAuthorization();
        authorization.id = id;
        authorization.evaluationId = evaluationId;
        authorization.vehicleId = vehicleId;
        authorization.status = status;
        authorization.override = override;
        authorization.overrideReason = overrideReason;
        authorization.authorizedBy = authorizedBy;
        authorization.authorizedAt = authorizedAt;
        authorization.validUntil = validUntil;
        authorization.createdAt = createdAt;
        authorization.updatedAt = updatedAt;
        return authorization;
    }

    public boolean isBlocked() {
        return status == AuthorizationStatus.NOT_ENABLED;
    }

    public boolean liftsBlock() {
        return override;
    }

    public boolean isValidOn(Instant moment) {
        return validUntil == null || !moment.isAfter(validUntil);
    }
}
