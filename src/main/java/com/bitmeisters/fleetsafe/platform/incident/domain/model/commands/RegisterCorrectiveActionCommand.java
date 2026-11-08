package com.bitmeisters.fleetsafe.platform.incident.domain.model.commands;

import java.time.Instant;
import java.util.UUID;

public record RegisterCorrectiveActionCommand(
        UUID incidentId,
        String description,
        UUID performedBy,
        Instant performedAt,
        String evidenceUrl) {

    public RegisterCorrectiveActionCommand {
        if (incidentId == null) {
            throw new IllegalArgumentException("incident.error.id.required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("incident.corrective-action.error.description.required");
        }
        if (performedBy == null) {
            throw new IllegalArgumentException("incident.corrective-action.error.performed-by.required");
        }
        if (performedAt == null) {
            throw new IllegalArgumentException("incident.corrective-action.error.performed-at.required");
        }
        evidenceUrl = evidenceUrl == null ? "" : evidenceUrl;
    }
}