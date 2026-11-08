package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.assemblers;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.IncidentFollowUp;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources.IncidentFollowUpResource;

public class IncidentFollowUpAssembler {

    public IncidentFollowUpResource toResourceFromEntity(IncidentFollowUp entity) {
        return new IncidentFollowUpResource(
                entity.getId(),
                entity.getIncidentId(),
                entity.getNote(),
                entity.getCreatedBy(),
                entity.getCreatedAt()
        );
    }
}