package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects;

import java.util.UUID;

public record InspectionItemId(UUID value) {

    public InspectionItemId {
        if (value == null) {
            throw new IllegalArgumentException("evaluation.rule.error.inspection-item-id.required");
        }
    }
}
