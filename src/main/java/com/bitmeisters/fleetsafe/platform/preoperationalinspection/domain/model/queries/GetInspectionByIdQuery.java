package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionId;

/**
 * Query to retrieve a pre-operational inspection by its unique identifier.
 */
public record GetInspectionByIdQuery(InspectionId inspectionId) {

    public GetInspectionByIdQuery {
        if (inspectionId == null) {
            throw new IllegalArgumentException("inspection.error.id.required");
        }
    }
}
