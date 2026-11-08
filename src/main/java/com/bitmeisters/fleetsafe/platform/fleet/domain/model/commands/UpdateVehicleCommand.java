package com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.LoadCapacity;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.ManufacturingYear;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleBrand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleModel;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleType;

/**
 * Changes the information of a vehicle. The plate, the fleet and the status are not editable here.
 */
public record UpdateVehicleCommand(
        CompanyId companyId,
        VehicleId vehicleId,
        VehicleBrand brand,
        VehicleModel model,
        ManufacturingYear year,
        VehicleType type,
        LoadCapacity capacity) {

    public UpdateVehicleCommand {
        if (companyId == null) throw new IllegalArgumentException("fleet.error.company-id.required");
        if (vehicleId == null) throw new IllegalArgumentException("fleet.vehicle.error.id.required");
        if (brand == null) throw new IllegalArgumentException("fleet.vehicle.error.brand.required");
        if (model == null) throw new IllegalArgumentException("fleet.vehicle.error.model.required");
        if (year == null) throw new IllegalArgumentException("fleet.vehicle.error.year.required");
        if (type == null) throw new IllegalArgumentException("fleet.vehicle.error.type.required");
        if (capacity == null) throw new IllegalArgumentException("fleet.vehicle.error.capacity.required");
    }
}
