package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.Repair;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.entities.RepairPersistenceEntity;

public class RepairPersistenceAssembler {

    private RepairPersistenceAssembler() {
    }

    public static Repair toDomainFromPersistence(RepairPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }

        return Repair.restore(
                entity.getId(),
                entity.getIncidentId(),
                entity.getWorkshop(),
                entity.getCost(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getStatus());
    }

    public static RepairPersistenceEntity toPersistenceFromDomain(Repair repair) {
        if (repair == null) {
            return null;
        }

        var entity = new RepairPersistenceEntity();

        if (repair.getId() != null) {
            entity.setId(repair.getId());
        }

        entity.setIncidentId(repair.getIncidentId());
        entity.setWorkshop(repair.getWorkshop());
        entity.setCost(repair.getCost());
        entity.setStartedAt(repair.getStartedAt());
        entity.setFinishedAt(repair.getFinishedAt());
        entity.setStatus(repair.getStatus());

        return entity;
    }
}