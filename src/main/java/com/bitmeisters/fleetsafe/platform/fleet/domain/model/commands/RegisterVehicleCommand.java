package com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.FleetId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.LoadCapacity;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.ManufacturingYear;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.Plate;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleBrand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleModel;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleType;

/**
 * Registers a vehicle in a fleet of the company of the signed-in user. The status is not part of it:
 * a vehicle always starts enabled and only evaluations change it afterwards.
 */
public record RegisterVehicleCommand(
        CompanyId companyId,
        FleetId fleetId,
        Plate plate,
        VehicleBrand brand,
        VehicleModel model,
        ManufacturingYear year,
        VehicleType type,
        LoadCapacity capacity) {

    public RegisterVehicleCommand {
        if (companyId == null) throw new IllegalArgumentException("fleet.error.company-id.required");
        if (fleetId == null) throw new IllegalArgumentException("fleet.error.fleet-id.required");
        if (plate == null) throw new IllegalArgumentException("fleet.vehicle.error.plate.required");
        if (brand == null) throw new IllegalArgumentException("fleet.vehicle.error.brand.required");
        if (model == null) throw new IllegalArgumentException("fleet.vehicle.error.model.required");
        if (year == null) throw new IllegalArgumentException("fleet.vehicle.error.year.required");
        if (type == null) throw new IllegalArgumentException("fleet.vehicle.error.type.required");
        if (capacity == null) throw new IllegalArgumentException("fleet.vehicle.error.capacity.required");
    }
}
