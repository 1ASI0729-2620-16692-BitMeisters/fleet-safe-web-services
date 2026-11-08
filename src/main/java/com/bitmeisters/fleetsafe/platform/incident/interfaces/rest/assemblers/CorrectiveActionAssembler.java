package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.assemblers;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.CorrectiveAction;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources.CorrectiveActionResource;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class CorrectiveActionAssembler {

    private final Function<UUID, String> nameOf;
    private final UnaryOperator<String> readUrlOf;

    public CorrectiveActionAssembler(Function<UUID, String> nameOf, UnaryOperator<String> readUrlOf) {
        this.nameOf = nameOf;
        this.readUrlOf = readUrlOf;
    }

    public CorrectiveActionResource toResourceFromEntity(CorrectiveAction entity) {
        return new CorrectiveActionResource(
                entity.getId(),
                entity.getIncidentId(),
                entity.getDescription(),
                entity.getPerformedBy(),
                nameOf.apply(entity.getPerformedBy()),
                entity.getPerformedAt(),
                readUrlOf.apply(entity.getEvidenceUrl())
        );
    }
}