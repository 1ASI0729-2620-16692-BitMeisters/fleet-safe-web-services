package com.bitmeisters.fleetsafe.platform.incident.application.queryservices;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.Incident;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.CorrectiveAction;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.IncidentFollowUp;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.Repair;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetCorrectiveActionsByIncidentIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentByIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentFollowUpsQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentsByStatusQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentsByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentsQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetRepairsByIncidentIdQuery;

import java.util.List;
import java.util.Optional;

public interface IncidentQueryService {

    List<Incident> handle(GetIncidentsQuery query);

    Optional<Incident> handle(GetIncidentByIdQuery query);

    List<Incident> handle(GetIncidentsByVehicleIdQuery query);

    List<Incident> handle(GetIncidentsByStatusQuery query);

    List<CorrectiveAction> handle(GetCorrectiveActionsByIncidentIdQuery query);

    List<Repair> handle(GetRepairsByIncidentIdQuery query);

    List<IncidentFollowUp> handle(GetIncidentFollowUpsQuery query);
}