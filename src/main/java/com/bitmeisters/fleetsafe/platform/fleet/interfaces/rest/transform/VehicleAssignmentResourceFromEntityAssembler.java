package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.application.queryservices.ActiveVehicleAssignment;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Vehicle;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.VehicleAssignment;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.DriverVehicleAssignmentResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.VehicleAssignmentResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.VehicleSummaryResource;

public class VehicleAssignmentResourceFromEntityAssembler {

    public static VehicleAssignmentResource toResourceFromEntity(VehicleAssignment entity) {
        return new VehicleAssignmentResource(
                entity.getId(),
                entity.getVehicleId().value(),
                entity.getDriverId().value(),
                entity.getAssignedFrom(),
                entity.getAssignedTo(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static DriverVehicleAssignmentResource toDriverResourceFromEntity(ActiveVehicleAssignment active) {
        var assignment = active.assignment();
        return new DriverVehicleAssignmentResource(
                assignment.getId(),
                assignment.getDriverId().value(),
                assignment.getAssignedFrom(),
                assignment.getAssignedTo(),
                assignment.isActive(),
                assignment.getCreatedAt(),
                toVehicleSummary(active.vehicle()));
    }

    private static VehicleSummaryResource toVehicleSummary(Vehicle vehicle) {
        return new VehicleSummaryResource(
                vehicle.getId(),
                vehicle.getPlate().value(),
                vehicle.getBrand().value(),
                vehicle.getModel().value(),
                vehicle.getYear().value(),
                vehicle.getType().name(),
                vehicle.getCurrentStatus().name());
    }
}
