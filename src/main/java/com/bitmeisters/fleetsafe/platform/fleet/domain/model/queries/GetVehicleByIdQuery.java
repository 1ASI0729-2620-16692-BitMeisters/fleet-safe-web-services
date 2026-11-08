package com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleId;

public record GetVehicleByIdQuery(CompanyId companyId, VehicleId vehicleId) {

    public GetVehicleByIdQuery {
        if (companyId == null) throw new IllegalArgumentException("fleet.error.company-id.required");
        if (vehicleId == null) throw new IllegalArgumentException("fleet.vehicle.error.id.required");
    }
}
