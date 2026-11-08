package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;

/**
 * Query to retrieve an inspection item by its unique identifier.
 */
public record GetInspectionItemByIdQuery(InspectionItemId id) {

    public GetInspectionItemByIdQuery {
        if (id == null) {
            throw new IllegalArgumentException("inspection.item.error.id.required");
        }
    }
}
