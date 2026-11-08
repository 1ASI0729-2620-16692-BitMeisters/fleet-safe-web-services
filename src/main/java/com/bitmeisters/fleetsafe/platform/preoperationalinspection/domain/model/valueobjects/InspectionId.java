package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects;

import java.util.UUID;

public record InspectionId(UUID value) {

    public InspectionId {
        if (value == null) {
            throw new IllegalArgumentException("inspection.error.id.required");
        }
    }

    public InspectionId(){
        this(UUID.randomUUID());
    }

}
