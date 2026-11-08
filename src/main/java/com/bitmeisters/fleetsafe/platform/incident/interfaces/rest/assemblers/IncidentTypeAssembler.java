package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.assemblers;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.IncidentType;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources.IncidentTypeResource;

public class IncidentTypeAssembler {

    public IncidentTypeResource toResourceFromEntity(IncidentType entity) {
        return new IncidentTypeResource(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.isActive()
        );
    }
}