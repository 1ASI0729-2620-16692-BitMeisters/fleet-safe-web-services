package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects;

import java.util.UUID;

public record VehicleId(UUID value) {

    public VehicleId {
        if (value == null) {
            throw new IllegalArgumentException("evaluation.error.vehicle-id.required");
        }
    }
}
