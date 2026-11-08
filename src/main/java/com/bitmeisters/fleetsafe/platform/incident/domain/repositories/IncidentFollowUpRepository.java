package com.bitmeisters.fleetsafe.platform.incident.domain.repositories;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.IncidentFollowUp;

import java.util.List;
import java.util.UUID;

public interface IncidentFollowUpRepository {

    List<IncidentFollowUp> findAllByIncidentIdOrderByCreatedAtDesc(
            UUID incidentId
    );

    IncidentFollowUp save(IncidentFollowUp followUp);
}