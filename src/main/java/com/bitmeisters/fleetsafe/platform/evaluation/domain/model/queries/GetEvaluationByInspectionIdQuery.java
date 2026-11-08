package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionId;

public record GetEvaluationByInspectionIdQuery(InspectionId inspectionId) {

    public GetEvaluationByInspectionIdQuery {
        if (inspectionId == null) throw new IllegalArgumentException("evaluation.error.inspection-id.required");
    }
}
