package com.bitmeisters.fleetsafe.platform.incident.application.queryservices;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.IncidentType;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentTypeByIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentTypesQuery;

import java.util.List;
import java.util.Optional;

public interface IncidentTypeQueryService {

    List<IncidentType> handle(GetIncidentTypesQuery query);

    Optional<IncidentType> handle(GetIncidentTypeByIdQuery query);
}