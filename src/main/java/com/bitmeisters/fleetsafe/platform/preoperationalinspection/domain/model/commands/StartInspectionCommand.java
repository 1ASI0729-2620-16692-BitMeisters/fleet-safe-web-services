package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.Odometer;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.VehicleId;

/**
 * Command to start a new vehicle pre-operational inspection.
 */
public record StartInspectionCommand(
        VehicleId vehicleId,
        DriverId driverId,
        Odometer odometer) {

    public StartInspectionCommand {
        if (vehicleId == null) {
            throw new IllegalArgumentException("inspection.error.vehicle-id.required");
        }
        if (driverId == null) {
            throw new IllegalArgumentException("inspection.error.driver-id.required");
        }
        if (odometer == null) {
            throw new IllegalArgumentException("inspection.error.odometer.invalid");
        }
    }
}
