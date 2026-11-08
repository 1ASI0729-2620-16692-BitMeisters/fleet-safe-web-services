package com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionStatus;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.entities.InspectionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA persistence repository for vehicle pre-operational inspections.
 */
@Repository
public interface InspectionPersistenceRepository extends JpaRepository<InspectionPersistenceEntity, UUID> {

    /**
     * Checks if a vehicle has an inspection with the specified status.
     *
     * @param vehicleId the vehicle identifier
     * @param status    the inspection status
     * @return true if exists, false otherwise
     */
    boolean existsByVehicleIdAndStatus(UUID vehicleId, InspectionStatus status);

    /**
     * Retrieves all inspections for a vehicle ordered from newest to oldest.
     *
     * @param vehicleId the vehicle identifier
     * @return ordered list of inspections
     */
    List<InspectionPersistenceEntity> findAllByVehicleIdOrderByStartedAtDesc(UUID vehicleId);

    /**
     * Retrieves all inspections conducted by a driver ordered from newest to oldest.
     *
     * @param driverId the driver identifier
     * @return ordered list of inspections
     */
    List<InspectionPersistenceEntity> findAllByDriverIdOrderByStartedAtDesc(UUID driverId);
}
