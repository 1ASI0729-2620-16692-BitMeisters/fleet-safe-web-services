package com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleStatus;

/**
 * Vehicles of a company. When a status is given, only the vehicles in that status are returned.
 */
public record GetVehiclesByCompanyIdQuery(CompanyId companyId, VehicleStatus status) {

    public GetVehiclesByCompanyIdQuery {
        if (companyId == null) throw new IllegalArgumentException("fleet.error.company-id.required");
    }
}
