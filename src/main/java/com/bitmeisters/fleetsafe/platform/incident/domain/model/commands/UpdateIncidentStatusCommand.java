package com.bitmeisters.fleetsafe.platform.incident.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentStatus;

import java.util.UUID;

public record UpdateIncidentStatusCommand(
        UUID incidentId,
        IncidentStatus status
) {
    public UpdateIncidentStatusCommand {
        if (incidentId == null) {
            throw new IllegalArgumentException("incident.error.id.required");
        }

        if (status != IncidentStatus.IN_REVIEW) {
            throw new IllegalArgumentException(
                    "incident.error.status.invalid");
        }
    }
}