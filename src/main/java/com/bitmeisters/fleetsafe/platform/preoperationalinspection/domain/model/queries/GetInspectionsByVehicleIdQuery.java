package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.VehicleId;

/**
 * Query to retrieve inspections performed on a specific vehicle.
 */
public record GetInspectionsByVehicleIdQuery(VehicleId vehicleId) {

    public GetInspectionsByVehicleIdQuery {
        if (vehicleId == null) {
            throw new IllegalArgumentException("inspection.error.vehicle-id.required");
        }
    }
}
