package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentStatus;

import java.time.Instant;

public record UpdateIncidentStatusResource(
        IncidentStatus status,
        String resolutionType,
        Instant resolvedAt
) {
}