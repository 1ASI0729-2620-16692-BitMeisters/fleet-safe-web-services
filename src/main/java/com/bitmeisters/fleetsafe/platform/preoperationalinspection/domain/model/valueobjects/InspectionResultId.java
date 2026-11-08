package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects;

import java.util.UUID;
public record InspectionResultId(UUID value) {

    public InspectionResultId {
        if (value == null) {
            throw new IllegalArgumentException("inspection.result.error.id.required");
        }
    }

    public InspectionResultId(){
        this(UUID.randomUUID());
    }

}
