package com.bitmeisters.fleetsafe.platform.incident.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentOrigin;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentSeverity;

import java.util.UUID;

public record ReportIncidentCommand(
        UUID vehicleId,
        UUID incidentTypeId,
        UUID inspectionId,
        IncidentOrigin origin,
        String description,
        IncidentSeverity severity,
        UUID reportedBy) {

    public ReportIncidentCommand {
        if (vehicleId == null) {
            throw new IllegalArgumentException("incident.error.vehicle-id.required");
        }
        if (incidentTypeId == null) {
            throw new IllegalArgumentException("incident.error.incident-type-id.required");
        }
        if (origin == null) {
            throw new IllegalArgumentException("incident.error.origin.required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("incident.error.description.required");
        }
        if (severity == null) {
            throw new IllegalArgumentException("incident.error.severity.required");
        }
        if (reportedBy == null) {
            throw new IllegalArgumentException("incident.error.reported-by.required");
        }
    }
}