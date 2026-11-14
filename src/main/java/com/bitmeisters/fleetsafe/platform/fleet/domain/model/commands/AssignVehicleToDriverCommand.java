package com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleId;

/**
 * Hands a vehicle of the company to a driver of the company, starting today.
 */
public record AssignVehicleToDriverCommand(CompanyId companyId, VehicleId vehicleId, DriverId driverId) {

    public AssignVehicleToDriverCommand {
        if (companyId == null) throw new IllegalArgumentException("fleet.error.company-id.required");
        if (vehicleId == null) throw new IllegalArgumentException("fleet.vehicle.error.id.required");
        if (driverId == null) throw new IllegalArgumentException("fleet.driver.error.id.required");
    }
}
