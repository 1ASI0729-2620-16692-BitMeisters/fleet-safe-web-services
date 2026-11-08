package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;

public record GetAuthorizationHistoryByVehicleIdQuery(VehicleId vehicleId) {

    public GetAuthorizationHistoryByVehicleIdQuery {
        if (vehicleId == null) throw new IllegalArgumentException("evaluation.error.vehicle-id.required");
    }
}
