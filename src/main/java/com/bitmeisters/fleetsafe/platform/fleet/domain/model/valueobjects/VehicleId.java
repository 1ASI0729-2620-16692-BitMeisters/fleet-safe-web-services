package com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects;

import java.util.UUID;

public record VehicleId(UUID value) {

    public VehicleId {
        if (value == null) {
            throw new IllegalArgumentException("fleet.vehicle.error.id.required");
        }
    }
}
