package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects;

import java.util.UUID;

public record ObservationId(UUID value) {

    public ObservationId {
        if (value == null) {
            throw new IllegalArgumentException("inspection.observation.error.id.required");
        }
    }

    public ObservationId(){
        this(UUID.randomUUID());
    }

}
