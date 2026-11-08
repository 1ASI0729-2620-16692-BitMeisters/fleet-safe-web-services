package com.bitmeisters.fleetsafe.platform.incident.domain.model.commands;

import java.util.UUID;

public record UpdateIncidentTypeCommand(
        UUID incidentTypeId,
        String code,
        String name,
        String description,
        Boolean active) {

    public UpdateIncidentTypeCommand {
        if (incidentTypeId == null) {
            throw new IllegalArgumentException("incident.type.error.id.required");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("incident.type.error.code.required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("incident.type.error.name.required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("incident.type.error.description.required");
        }
        if (active == null) {
            throw new IllegalArgumentException("incident.type.error.active.required");
        }
    }
}