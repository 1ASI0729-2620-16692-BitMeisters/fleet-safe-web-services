package com.bitmeisters.fleetsafe.platform.incident.domain.model.queries;

import java.util.UUID;

public record GetRepairsByIncidentIdQuery(UUID incidentId) {
}