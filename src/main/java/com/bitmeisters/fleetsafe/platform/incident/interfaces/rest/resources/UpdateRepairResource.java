package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.RepairStatus;

import java.time.Instant;

public record UpdateRepairResource(
        RepairStatus status,
        Instant finishedAt
) {
}