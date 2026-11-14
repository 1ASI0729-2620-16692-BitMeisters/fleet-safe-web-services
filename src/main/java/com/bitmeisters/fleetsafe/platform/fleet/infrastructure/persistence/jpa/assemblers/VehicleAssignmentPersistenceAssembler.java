package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.VehicleAssignment;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities.VehicleAssignmentPersistenceEntity;

public final class VehicleAssignmentPersistenceAssembler {

    private VehicleAssignmentPersistenceAssembler() {
    }

    public static VehicleAssignment toDomainFromPersistence(VehicleAssignmentPersistenceEntity entity) {
        if (entity == null) return null;
        return VehicleAssignment.restore(
                entity.getId(),
                new VehicleId(entity.getVehicleId()),
                new DriverId(entity.getDriverId()),
                entity.getAssignedFrom(),
                entity.getAssignedTo(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static VehicleAssignmentPersistenceEntity toPersistenceFromDomain(VehicleAssignment assignment) {
        if (assignment == null) return null;
        var entity = new VehicleAssignmentPersistenceEntity();
        if (assignment.getId() != null) {
            entity.setId(assignment.getId());
            entity.setCreatedAt(assignment.getCreatedAt());
        }
        entity.setVehicleId(assignment.getVehicleId().value());
        entity.setDriverId(assignment.getDriverId().value());
        entity.setAssignedFrom(assignment.getAssignedFrom());
        entity.setAssignedTo(assignment.getAssignedTo());
        entity.setActive(assignment.isActive());
        return entity;
    }
}
