package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources;

import java.util.UUID;

public record IncidentTypeResource(
        UUID id,
        String code,
        String name,
        String description,
        boolean isActive
) {
}