package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.assemblers;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.Repair;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources.RepairResource;

public class RepairAssembler {

    public RepairResource toResourceFromEntity(Repair entity) {
        return new RepairResource(
                entity.getId(),
                entity.getIncidentId(),
                entity.getWorkshop(),
                entity.getCost(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getStatus()
        );
    }
}