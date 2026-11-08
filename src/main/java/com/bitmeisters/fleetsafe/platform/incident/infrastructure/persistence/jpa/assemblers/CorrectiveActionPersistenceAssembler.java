package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.CorrectiveAction;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.entities.CorrectiveActionPersistenceEntity;

public class CorrectiveActionPersistenceAssembler {

    private CorrectiveActionPersistenceAssembler() {
    }

    public static CorrectiveAction toDomainFromPersistence(
            CorrectiveActionPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }

        return CorrectiveAction.restore(
                entity.getId(),
                entity.getIncidentId(),
                entity.getDescription(),
                entity.getPerformedBy(),
                entity.getPerformedAt(),
                entity.getEvidenceUrl());
    }

    public static CorrectiveActionPersistenceEntity toPersistenceFromDomain(
            CorrectiveAction correctiveAction) {
        if (correctiveAction == null) {
            return null;
        }

        var entity = new CorrectiveActionPersistenceEntity();

        if (correctiveAction.getId() != null) {
            entity.setId(correctiveAction.getId());
        }

        entity.setIncidentId(correctiveAction.getIncidentId());
        entity.setDescription(correctiveAction.getDescription());
        entity.setPerformedBy(correctiveAction.getPerformedBy());
        entity.setPerformedAt(correctiveAction.getPerformedAt());
        entity.setEvidenceUrl(correctiveAction.getEvidenceUrl());

        return entity;
    }
}