package com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects;

import java.util.UUID;

public record VehicleId(UUID value) {

    public VehicleId {
        if (value == null) {
            throw new IllegalArgumentException("documentation.error.vehicle-id.required");
        }
    }
}
