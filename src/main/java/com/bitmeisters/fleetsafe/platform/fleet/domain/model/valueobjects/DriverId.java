package com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects;

import java.util.UUID;

public record DriverId(UUID value) {

    public DriverId {
        if (value == null) {
            throw new IllegalArgumentException("fleet.driver.error.id.required");
        }
    }
}
