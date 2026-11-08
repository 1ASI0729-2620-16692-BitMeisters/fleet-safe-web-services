package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.IncidentFollowUp;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.entities.IncidentFollowUpPersistenceEntity;

public class IncidentFollowUpPersistenceAssembler {

    private IncidentFollowUpPersistenceAssembler() {
    }

    public static IncidentFollowUp toDomainFromPersistence(
            IncidentFollowUpPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }

        return IncidentFollowUp.restore(
                entity.getId(),
                entity.getIncidentId(),
                entity.getNote(),
                entity.getCreatedBy(),
                entity.getCreatedAt());
    }

    public static IncidentFollowUpPersistenceEntity toPersistenceFromDomain(
            IncidentFollowUp followUp) {
        if (followUp == null) {
            return null;
        }

        var entity = new IncidentFollowUpPersistenceEntity();

        if (followUp.getId() != null) {
            entity.setId(followUp.getId());
        }

        entity.setIncidentId(followUp.getIncidentId());
        entity.setNote(followUp.getNote());
        entity.setCreatedBy(followUp.getCreatedBy());
        entity.setCreatedAt(followUp.getCreatedAt());

        return entity;
    }
}