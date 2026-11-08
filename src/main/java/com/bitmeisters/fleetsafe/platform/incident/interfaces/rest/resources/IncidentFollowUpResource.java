package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record IncidentFollowUpResource(
        UUID id,
        UUID incidentId,
        String note,
        UUID createdBy,
        Instant createdAt
) {
}