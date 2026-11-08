package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Vehicle;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.FleetId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.LoadCapacity;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.ManufacturingYear;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.Plate;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleBrand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleModel;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities.VehiclePersistenceEntity;

public final class VehiclePersistenceAssembler {

    private VehiclePersistenceAssembler() {
    }

    public static Vehicle toDomainFromPersistence(VehiclePersistenceEntity entity) {
        if (entity == null) return null;
        return Vehicle.restore(
                entity.getId(),
                new FleetId(entity.getFleetId()),
                new Plate(entity.getPlate()),
                new VehicleBrand(entity.getBrand()),
                new VehicleModel(entity.getModel()),
                new ManufacturingYear(entity.getYear()),
                entity.getType(),
                new LoadCapacity(entity.getCapacity()),
                entity.getCurrentStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static VehiclePersistenceEntity toPersistenceFromDomain(Vehicle vehicle) {
        if (vehicle == null) return null;
        var entity = new VehiclePersistenceEntity();
        if (vehicle.getId() != null) {
            entity.setId(vehicle.getId());
            entity.setCreatedAt(vehicle.getCreatedAt());
        }
        entity.setFleetId(vehicle.getFleetId().value());
        entity.setPlate(vehicle.getPlate().value());
        entity.setBrand(vehicle.getBrand().value());
        entity.setModel(vehicle.getModel().value());
        entity.setYear(vehicle.getYear().value());
        entity.setType(vehicle.getType());
        entity.setCapacity(vehicle.getCapacity().value());
        entity.setCurrentStatus(vehicle.getCurrentStatus());
        return entity;
    }
}
