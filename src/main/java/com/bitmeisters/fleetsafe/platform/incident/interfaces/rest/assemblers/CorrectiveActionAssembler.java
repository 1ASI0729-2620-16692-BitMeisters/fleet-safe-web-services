package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.assemblers;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.CorrectiveAction;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources.CorrectiveActionResource;

public class CorrectiveActionAssembler {

    public CorrectiveActionResource toResourceFromEntity(CorrectiveAction entity) {
        return new CorrectiveActionResource(
                entity.getId(),
                entity.getIncidentId(),
                entity.getDescription(),
                entity.getPerformedBy(),
                entity.getPerformedAt(),
                entity.getEvidenceUrl()
        );
    }
}