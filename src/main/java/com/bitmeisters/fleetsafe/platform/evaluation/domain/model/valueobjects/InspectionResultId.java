package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects;

import java.util.UUID;

public record InspectionResultId(UUID value) {

    public InspectionResultId {
        if (value == null) {
            throw new IllegalArgumentException("evaluation.error.inspection-result-id.required");
        }
    }
}
