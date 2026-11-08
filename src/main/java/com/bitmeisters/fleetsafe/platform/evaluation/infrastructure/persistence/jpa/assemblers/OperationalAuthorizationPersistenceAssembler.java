package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.entities.OperationalAuthorizationPersistenceEntity;

public final class OperationalAuthorizationPersistenceAssembler {

    private OperationalAuthorizationPersistenceAssembler() {
    }

    public static OperationalAuthorization toDomainFromPersistence(OperationalAuthorizationPersistenceEntity entity) {
        if (entity == null) return null;
        return OperationalAuthorization.restore(
                entity.getId(),
                entity.getEvaluationId(),
                new VehicleId(entity.getVehicleId()),
                entity.getStatus(),
                entity.isOverride(),
                entity.getOverrideReason(),
                entity.getAuthorizedBy(),
                entity.getAuthorizedAt(),
                entity.getValidUntil(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static OperationalAuthorizationPersistenceEntity toPersistenceFromDomain(OperationalAuthorization authorization) {
        if (authorization == null) return null;
        var entity = new OperationalAuthorizationPersistenceEntity();
        if (authorization.getId() != null) {
            entity.setId(authorization.getId());
            entity.setCreatedAt(authorization.getCreatedAt());
        }
        entity.setEvaluationId(authorization.getEvaluationId());
        entity.setVehicleId(authorization.getVehicleId().value());
        entity.setStatus(authorization.getStatus());
        entity.setOverride(authorization.isOverride());
        entity.setOverrideReason(authorization.getOverrideReason());
        entity.setAuthorizedBy(authorization.getAuthorizedBy());
        entity.setAuthorizedAt(authorization.getAuthorizedAt());
        entity.setValidUntil(authorization.getValidUntil());
        return entity;
    }
}
