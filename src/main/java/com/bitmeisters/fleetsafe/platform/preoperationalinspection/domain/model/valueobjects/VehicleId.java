package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects;

import java.util.UUID;
public record VehicleId(UUID value) {

    public VehicleId {
        if (value == null) {
            throw new IllegalArgumentException("inspection.error.vehicle-id.required");
        }
    }

}
