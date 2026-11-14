package com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects;

import java.util.UUID;

public record VehicleAssignmentId(UUID value) {

    public VehicleAssignmentId {
        if (value == null) {
            throw new IllegalArgumentException("fleet.assignment.error.id.required");
        }
    }
}
