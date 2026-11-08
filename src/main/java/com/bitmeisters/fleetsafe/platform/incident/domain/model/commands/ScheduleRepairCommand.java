package com.bitmeisters.fleetsafe.platform.incident.domain.model.commands;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ScheduleRepairCommand(
        UUID incidentId,
        String workshop,
        BigDecimal cost,
        Instant startedAt,
        Instant finishedAt) {

    public ScheduleRepairCommand {
        if (incidentId == null) {
            throw new IllegalArgumentException("incident.error.id.required");
        }
        if (workshop == null || workshop.isBlank()) {
            throw new IllegalArgumentException("incident.repair.error.workshop.required");
        }
        if (cost == null || cost.signum() < 0) {
            throw new IllegalArgumentException("incident.repair.error.cost.invalid");
        }
        if (startedAt == null) {
            throw new IllegalArgumentException("incident.repair.error.started-at.required");
        }
        if (finishedAt != null && finishedAt.isBefore(startedAt)) {
            throw new IllegalArgumentException("incident.repair.error.finished-at.invalid");
        }
    }
}