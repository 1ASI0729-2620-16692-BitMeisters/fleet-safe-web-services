package com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects;

import java.util.UUID;

public record FleetId(UUID value) {

    public FleetId {
        if (value == null) {
            throw new IllegalArgumentException("fleet.error.fleet-id.required");
        }
    }
}
