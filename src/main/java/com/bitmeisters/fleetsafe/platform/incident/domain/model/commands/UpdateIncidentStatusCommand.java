package com.bitmeisters.fleetsafe.platform.incident.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentStatus;

import java.time.Instant;
import java.util.UUID;

public record UpdateIncidentStatusCommand(
        UUID incidentId,
        IncidentStatus status,
        String resolutionType,
        Instant resolvedAt
) {
    public UpdateIncidentStatusCommand {
        if (incidentId == null) {
            throw new IllegalArgumentException("incident.error.id.required");
        }

        if (status != IncidentStatus.IN_REVIEW
                && status != IncidentStatus.RESOLVED) {
            throw new IllegalArgumentException(
                    "incident.error.status.invalid");
        }

        if (status == IncidentStatus.RESOLVED) {
            if (resolutionType == null || resolutionType.isBlank()) {
                throw new IllegalArgumentException(
                        "incident.error.resolution-type.required");
            }

            if (resolvedAt == null) {
                throw new IllegalArgumentException(
                        "incident.error.resolved-at.required");
            }
        }

        if (status == IncidentStatus.IN_REVIEW
                && (resolutionType != null || resolvedAt != null)) {
            throw new IllegalArgumentException(
                    "incident.error.status.invalid");
        }
    }
}