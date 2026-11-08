package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.RepairStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RepairResource(
        UUID id,
        UUID incidentId,
        String workshop,
        BigDecimal cost,
        Instant startedAt,
        Instant finishedAt,
        RepairStatus status
) {
}