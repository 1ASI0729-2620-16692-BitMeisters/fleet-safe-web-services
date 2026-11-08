package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.events;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.Inspection;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.VehicleId;

/**
 * InspectionCompletedEvent domain event.
 *
 * <p>
 * Emitted when a driver successfully completes a pre-operational inspection.
 * Triggers the evaluation rules engine in the Evaluation bounded context.
 * </p>
 *
 * @param inspectionId the unique identifier of the completed inspection
 * @param vehicleId    the inspected vehicle identifier
 * @param driverId     the driver who conducted the inspection
 */
public record InspectionCompletedEvent(InspectionId inspectionId, VehicleId vehicleId, DriverId driverId) {

    /**
     * Convenience constructor extracting references from an Inspection aggregate.
     *
     * @param inspection the completed inspection aggregate
     */
    public InspectionCompletedEvent(Inspection inspection) {
        this(inspection.getId(), inspection.getVehicleId(), inspection.getDriverId());
    }
}