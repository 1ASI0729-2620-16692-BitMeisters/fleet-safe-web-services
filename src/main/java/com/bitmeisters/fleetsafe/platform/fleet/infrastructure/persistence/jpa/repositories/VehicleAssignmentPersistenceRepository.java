package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities.VehicleAssignmentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * An assignment belongs to a company through its vehicle, and the vehicle through its fleet, so the
 * queries by company look both up.
 */
@Repository
public interface VehicleAssignmentPersistenceRepository extends JpaRepository<VehicleAssignmentPersistenceEntity, UUID> {

    boolean existsByVehicleIdAndActiveTrue(UUID vehicleId);

    boolean existsByDriverIdAndActiveTrue(UUID driverId);

    Optional<VehicleAssignmentPersistenceEntity> findByDriverIdAndActiveTrue(UUID driverId);

    @Query("""
            select a from VehicleAssignmentPersistenceEntity a
            where a.id = :id
              and a.vehicleId in (select v.id from VehiclePersistenceEntity v
                                  where v.fleetId in (select f.id from FleetPersistenceEntity f where f.companyId = :companyId))
            """)
    Optional<VehicleAssignmentPersistenceEntity> findByIdAndCompanyId(@Param("id") UUID id, @Param("companyId") UUID companyId);

    @Query("""
            select a from VehicleAssignmentPersistenceEntity a
            where a.vehicleId in (select v.id from VehiclePersistenceEntity v
                                  where v.fleetId in (select f.id from FleetPersistenceEntity f where f.companyId = :companyId))
            order by a.assignedFrom desc, a.createdAt desc
            """)
    List<VehicleAssignmentPersistenceEntity> findAllByCompanyId(@Param("companyId") UUID companyId);

    @Query("""
            select a from VehicleAssignmentPersistenceEntity a
            where a.active = true
              and a.vehicleId in (select v.id from VehiclePersistenceEntity v
                                  where v.fleetId in (select f.id from FleetPersistenceEntity f where f.companyId = :companyId))
            order by a.assignedFrom desc, a.createdAt desc
            """)
    List<VehicleAssignmentPersistenceEntity> findAllActiveByCompanyId(@Param("companyId") UUID companyId);
}
