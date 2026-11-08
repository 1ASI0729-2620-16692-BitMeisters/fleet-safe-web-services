package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.IncidentType;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.entities.IncidentTypePersistenceEntity;

public class IncidentTypePersistenceAssembler {

    private IncidentTypePersistenceAssembler() {
    }

    public static IncidentType toDomainFromPersistence(IncidentTypePersistenceEntity entity) {
        if (entity == null) {
            return null;
        }

        return IncidentType.restore(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static IncidentTypePersistenceEntity toPersistenceFromDomain(IncidentType incidentType) {
        if (incidentType == null) {
            return null;
        }

        var entity = new IncidentTypePersistenceEntity();

        if (incidentType.getId() != null) {
            entity.setId(incidentType.getId());
            entity.setCreatedAt(incidentType.getCreatedAt());
        }

        entity.setCode(incidentType.getCode());
        entity.setName(incidentType.getName());
        entity.setDescription(incidentType.getDescription());
        entity.setActive(incidentType.isActive());

        return entity;
    }
}