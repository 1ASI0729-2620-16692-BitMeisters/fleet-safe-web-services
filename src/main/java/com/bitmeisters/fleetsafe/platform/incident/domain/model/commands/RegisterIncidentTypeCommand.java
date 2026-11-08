package com.bitmeisters.fleetsafe.platform.incident.domain.model.commands;

public record RegisterIncidentTypeCommand(
        String code,
        String name,
        String description) {

    public RegisterIncidentTypeCommand {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("incident.type.error.code.required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("incident.type.error.name.required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("incident.type.error.description.required");
        }
    }
}