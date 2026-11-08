package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;

import java.util.UUID;

public record LiftBlockCommand(VehicleId vehicleId, String reason, UUID authorizedBy) {

    public static final int REASON_MAX_LENGTH = 500;

    public LiftBlockCommand {
        if (vehicleId == null) throw new IllegalArgumentException("evaluation.error.vehicle-id.required");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("evaluation.authorization.error.reason.required");
        if (reason.length() > REASON_MAX_LENGTH) throw new IllegalArgumentException("evaluation.authorization.error.reason.size");
        if (authorizedBy == null) throw new IllegalArgumentException("evaluation.authorization.error.authorized-by.required");
    }
}
