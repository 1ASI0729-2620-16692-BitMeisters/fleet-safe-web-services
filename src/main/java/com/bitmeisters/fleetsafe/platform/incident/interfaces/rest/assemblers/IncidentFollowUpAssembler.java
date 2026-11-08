package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.assemblers;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.IncidentFollowUp;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources.IncidentFollowUpResource;

import java.util.UUID;
import java.util.function.Function;

public class IncidentFollowUpAssembler {

    private final Function<UUID, String> nameOf;

    public IncidentFollowUpAssembler(Function<UUID, String> nameOf) {
        this.nameOf = nameOf;
    }

    public IncidentFollowUpResource toResourceFromEntity(IncidentFollowUp entity) {
        return new IncidentFollowUpResource(
                entity.getId(),
                entity.getIncidentId(),
                entity.getNote(),
                entity.getCreatedBy(),
                nameOf.apply(entity.getCreatedBy()),
                entity.getCreatedAt()
        );
    }
}