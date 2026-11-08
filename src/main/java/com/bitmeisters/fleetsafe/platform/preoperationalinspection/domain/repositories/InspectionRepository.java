package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.repositories;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.Inspection;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionStatus;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.VehicleId;

import java.util.List;
import java.util.Optional;

/**
 * InspectionRepository port interface.
 *
 * <p>
 * Defines domain repository operations for persisting and retrieving vehicle inspections.
 * </p>
 */
public interface InspectionRepository {

    /**
     * Finds an inspection by its unique identifier.
     *
     * @param id the inspection identifier
     * @return optional containing the inspection, or empty
     */
    Optional<Inspection> findById(InspectionId id);

    /**
     * Retrieves all inspections.
     *
     * @return list of all inspections
     */
    List<Inspection> findAll();

    /**
     * Checks if a vehicle has an ongoing inspection with the given status.
     *
     * @param vehicleId the vehicle identifier
     * @param status    the status to check (e.g., IN_PROGRESS)
     * @return true if an inspection matches, false otherwise
     */
    boolean existsByVehicleIdAndStatus(VehicleId vehicleId, InspectionStatus status);

    /**
     * Retrieves all inspections for a vehicle ordered from newest to oldest.
     *
     * @param vehicleId the vehicle identifier
     * @return list of vehicle inspections
     */
    List<Inspection> findAllByVehicleIdOrderByStartedAtDesc(VehicleId vehicleId);

    /**
     * Retrieves all inspections performed by a driver ordered from newest to oldest.
     *
     * @param driverId the driver identifier
     * @return list of driver inspections
     */
    List<Inspection> findAllByDriverIdOrderByStartedAtDesc(DriverId driverId);

    /**
     * Saves an inspection aggregate root.
     *
     * @param inspection the inspection to save
     * @return the saved inspection
     */
    Inspection save(Inspection inspection);
}