package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleStatus;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities.VehiclePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A vehicle belongs to a company through its fleet, so the queries by company look the fleets up.
 */
@Repository
public interface VehiclePersistenceRepository extends JpaRepository<VehiclePersistenceEntity, UUID> {

    boolean existsByPlate(String plate);

    @Query("""
            select v from VehiclePersistenceEntity v
            where v.id = :id
              and v.fleetId in (select f.id from FleetPersistenceEntity f where f.companyId = :companyId)
            """)
    Optional<VehiclePersistenceEntity> findByIdAndCompanyId(@Param("id") UUID id, @Param("companyId") UUID companyId);

    @Query("""
            select v from VehiclePersistenceEntity v
            where v.fleetId in (select f.id from FleetPersistenceEntity f where f.companyId = :companyId)
            order by v.plate
            """)
    List<VehiclePersistenceEntity> findAllByCompanyId(@Param("companyId") UUID companyId);

    @Query("""
            select v from VehiclePersistenceEntity v
            where v.fleetId in (select f.id from FleetPersistenceEntity f where f.companyId = :companyId)
              and v.currentStatus = :status
            order by v.plate
            """)
    List<VehiclePersistenceEntity> findAllByCompanyIdAndStatus(@Param("companyId") UUID companyId,
                                                                @Param("status") VehicleStatus status);
}
