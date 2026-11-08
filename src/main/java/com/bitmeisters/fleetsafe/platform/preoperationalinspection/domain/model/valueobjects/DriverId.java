package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects;

import java.util.UUID;

public record DriverId(UUID value) {

    public DriverId {
        if (value == null) {
            throw new IllegalArgumentException("inspection.error.driver-id.required");
        }
    }
}