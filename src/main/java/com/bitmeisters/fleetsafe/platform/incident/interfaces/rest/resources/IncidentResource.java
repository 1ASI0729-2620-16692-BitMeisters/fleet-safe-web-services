package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentOrigin;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentSeverity;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record IncidentResource(
        UUID id,
        UUID vehicleId,
        UUID incidentTypeId,
        UUID inspectionId,
        IncidentOrigin origin,
        String description,
        IncidentSeverity severity,
        IncidentStatus status,
        UUID reportedBy,
        Instant reportedAt,
        String resolutionType,
        Instant resolvedAt,
        Instant createdAt,
        Instant updatedAt,
        List<CorrectiveActionResource> correctiveActions,
        List<RepairResource> repairs,
        List<IncidentFollowUpResource> followUps
) {
    public IncidentResource {
        correctiveActions = correctiveActions == null
                ? List.of()
                : List.copyOf(correctiveActions);

        repairs = repairs == null
                ? List.of()
                : List.copyOf(repairs);

        followUps = followUps == null
                ? List.of()
                : List.copyOf(followUps);
    }
}