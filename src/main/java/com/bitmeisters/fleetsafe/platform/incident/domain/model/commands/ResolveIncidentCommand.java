package com.bitmeisters.fleetsafe.platform.incident.domain.model.commands;

import java.time.Instant;
import java.util.UUID;

public record ResolveIncidentCommand(
        UUID incidentId,
        String resolutionType,
        Instant resolvedAt) {

    public ResolveIncidentCommand {
        if (incidentId == null) {
            throw new IllegalArgumentException("incident.error.id.required");
        }
        if (resolutionType == null || resolutionType.isBlank()) {
            throw new IllegalArgumentException("incident.error.resolution-type.required");
        }
        if (resolvedAt == null) {
            throw new IllegalArgumentException("incident.error.resolved-at.required");
        }
    }
}