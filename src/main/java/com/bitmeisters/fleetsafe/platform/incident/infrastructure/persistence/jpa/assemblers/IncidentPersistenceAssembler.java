package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.Incident;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.entities.IncidentPersistenceEntity;
import org.springframework.stereotype.Component;

@Component
public class IncidentPersistenceAssembler {

    public Incident toDomainFromPersistence(IncidentPersistenceEntity entity) {
        return Incident.restore(
                entity.getId(),
                entity.getVehicleId(),
                entity.getIncidentTypeId(),
                entity.getInspectionId(),
                entity.getOrigin(),
                entity.getDescription(),
                entity.getSeverity(),
                entity.getStatus(),
                entity.getReportedBy(),
                entity.getReportedAt(),
                entity.getResolutionType(),
                entity.getResolvedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public IncidentPersistenceEntity toPersistenceFromDomain(Incident incident) {
        IncidentPersistenceEntity entity = new IncidentPersistenceEntity();

        if (incident.getId() != null) {
            entity.setId(incident.getId());
        }

        entity.setVehicleId(incident.getVehicleId());
        entity.setIncidentTypeId(incident.getIncidentTypeId());
        entity.setInspectionId(incident.getInspectionId());
        entity.setOrigin(incident.getOrigin());
        entity.setDescription(incident.getDescription());
        entity.setSeverity(incident.getSeverity());
        entity.setStatus(incident.getStatus());
        entity.setReportedBy(incident.getReportedBy());
        entity.setReportedAt(incident.getReportedAt());
        entity.setResolutionType(incident.getResolutionType());
        entity.setResolvedAt(incident.getResolvedAt());

        if (incident.getCreatedAt() != null) {
            entity.setCreatedAt(incident.getCreatedAt());
        }

        return entity;
    }
}