package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects;

import java.util.UUID;

public record InspectionId(UUID value) {

    public InspectionId {
        if (value == null) {
            throw new IllegalArgumentException("evaluation.error.inspection-id.required");
        }
    }
}
