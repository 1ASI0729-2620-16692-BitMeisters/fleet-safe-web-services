package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects;

public record InspectionResultSnapshot(
        InspectionResultId inspectionResultId,
        InspectionItemId inspectionItemId,
        ResultValue value) {

    public InspectionResultSnapshot {
        if (inspectionResultId == null) throw new IllegalArgumentException("evaluation.error.inspection-result-id.required");
        if (inspectionItemId == null) throw new IllegalArgumentException("evaluation.rule.error.inspection-item-id.required");
        if (value == null) throw new IllegalArgumentException("evaluation.error.result-value.required");
    }
}
