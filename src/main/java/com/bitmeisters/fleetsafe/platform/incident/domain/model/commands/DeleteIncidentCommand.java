package com.bitmeisters.fleetsafe.platform.incident.domain.model.commands;

import java.util.UUID;

public record DeleteIncidentCommand(UUID incidentId) {

    public DeleteIncidentCommand {
        if (incidentId == null) {
            throw new IllegalArgumentException("incident.error.id.required");
        }
    }
}