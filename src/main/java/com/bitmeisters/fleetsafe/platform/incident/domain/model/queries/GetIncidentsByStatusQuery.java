package com.bitmeisters.fleetsafe.platform.incident.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentStatus;

public record GetIncidentsByStatusQuery(IncidentStatus status) {
}