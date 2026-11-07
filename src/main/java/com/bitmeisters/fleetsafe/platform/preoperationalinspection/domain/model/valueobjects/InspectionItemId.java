package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects;

import java.util.UUID;
public record InspectionItemId(UUID value) {

    public InspectionItemId {
        if (value == null) {
            throw new IllegalArgumentException("inspection.item.error.id.required");
        }
    }

    public InspectionItemId(){
        this(UUID.randomUUID());
    }

}
