package com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.VehicleId;

public record GetDocumentsByVehicleIdQuery(VehicleId vehicleId) {

    public GetDocumentsByVehicleIdQuery {
        if (vehicleId == null) throw new IllegalArgumentException("documentation.error.vehicle-id.required");
    }
}
