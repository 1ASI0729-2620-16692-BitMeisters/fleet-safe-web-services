package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources;

import java.time.Instant;
import java.util.UUID;

public record CorrectiveActionResource(
        UUID id,
        UUID incidentId,
        String description,
        UUID performedBy,
        Instant performedAt,
        String evidenceUrl
) {
}