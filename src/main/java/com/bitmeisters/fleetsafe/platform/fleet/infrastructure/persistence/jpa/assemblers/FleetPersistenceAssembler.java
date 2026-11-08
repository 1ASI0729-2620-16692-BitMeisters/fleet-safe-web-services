package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Fleet;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.FleetName;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities.FleetPersistenceEntity;

public final class FleetPersistenceAssembler {

    private FleetPersistenceAssembler() {
    }

    public static Fleet toDomainFromPersistence(FleetPersistenceEntity entity) {
        if (entity == null) return null;
        return Fleet.restore(
                entity.getId(),
                new CompanyId(entity.getCompanyId()),
                new FleetName(entity.getName()),
                entity.getDescription(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static FleetPersistenceEntity toPersistenceFromDomain(Fleet fleet) {
        if (fleet == null) return null;
        var entity = new FleetPersistenceEntity();
        if (fleet.getId() != null) {
            entity.setId(fleet.getId());
            entity.setCreatedAt(fleet.getCreatedAt());
        }
        entity.setCompanyId(fleet.getCompanyId().value());
        entity.setName(fleet.getName().value());
        entity.setDescription(fleet.getDescription());
        return entity;
    }
}
