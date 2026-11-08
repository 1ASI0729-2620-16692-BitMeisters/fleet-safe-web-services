package com.bitmeisters.fleetsafe.platform.incident.domain.repositories;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.CorrectiveAction;

import java.util.List;
import java.util.UUID;

public interface CorrectiveActionRepository {

    List<CorrectiveAction> findAllByIncidentId(UUID incidentId);

    CorrectiveAction save(CorrectiveAction correctiveAction);
}