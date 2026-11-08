package com.bitmeisters.fleetsafe.platform.incident.domain.model.commands;

import java.time.Instant;
import java.util.UUID;

public record CompleteRepairCommand(
        UUID incidentId,
        UUID repairId,
        Instant finishedAt) {

    public CompleteRepairCommand {
        if (incidentId == null) {
            throw new IllegalArgumentException("incident.error.id.required");
        }
        if (repairId == null) {
            throw new IllegalArgumentException("incident.repair.error.id.required");
        }
        if (finishedAt == null) {
            throw new IllegalArgumentException("incident.repair.error.finished-at.required");
        }
    }
}