package com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.queryservices;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.InspectionItem;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionItemByIdQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionItemsQuery;

import java.util.List;
import java.util.Optional;

/**
 * InspectionItemQueryService port interface.
 *
 * <p>
 * Declares query execution contracts for the inspection checklist catalog.
 * </p>
 */
public interface InspectionItemQueryService {

    /**
     * Retrieves all inspection items matching the query criteria.
     *
     * @param query the query containing optional filters
     * @return list of matching inspection items
     */
    List<InspectionItem> handle(GetInspectionItemsQuery query);

    /**
     * Retrieves an inspection item by its unique identifier.
     *
     * @param query the query containing the item identifier
     * @return optional containing the item if found, or empty
     */
    Optional<InspectionItem> handle(GetInspectionItemByIdQuery query);
}
