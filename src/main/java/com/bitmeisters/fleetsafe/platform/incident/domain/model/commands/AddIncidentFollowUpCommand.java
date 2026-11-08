package com.bitmeisters.fleetsafe.platform.incident.domain.model.commands;

import java.time.Instant;
import java.util.UUID;

public record AddIncidentFollowUpCommand(
        UUID incidentId,
        String note,
        UUID createdBy,
        Instant createdAt) {

    public AddIncidentFollowUpCommand {
        if (incidentId == null) {
            throw new IllegalArgumentException("incident.error.id.required");
        }
        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException("incident.follow-up.error.note.required");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("incident.follow-up.error.created-by.required");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("incident.follow-up.error.created-at.required");
        }
    }
}