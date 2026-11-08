package com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.queryservices;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.Inspection;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionByIdQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionsByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionsQuery;

import java.util.List;
import java.util.Optional;

/**
 * InspectionQueryService port interface.
 *
 * <p>
 * Declares query execution operations for inspecting vehicle safety evaluations.
 * </p>
 */
public interface InspectionQueryService {

    /**
     * Retrieves an inspection by its unique identifier.
     *
     * @param query the query containing the inspection ID
     * @return optional containing the inspection if found, or empty
     */
    Optional<Inspection> handle(GetInspectionByIdQuery query);

    /**
     * Retrieves all recorded vehicle inspections.
     *
     * @param query the query to list all inspections
     * @return list of inspections
     */
    List<Inspection> handle(GetInspectionsQuery query);

    /**
     * Retrieves all inspections performed on a specific vehicle.
     *
     * @param query the query containing the vehicle identifier
     * @return list of matching vehicle inspections
     */
    List<Inspection> handle(GetInspectionsByVehicleIdQuery query);
}
