package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.entities.OperationalAuthorizationPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperationalAuthorizationPersistenceRepository extends JpaRepository<OperationalAuthorizationPersistenceEntity, UUID> {

    Optional<OperationalAuthorizationPersistenceEntity> findFirstByVehicleIdOrderByAuthorizedAtDesc(UUID vehicleId);

    List<OperationalAuthorizationPersistenceEntity> findAllByVehicleIdOrderByAuthorizedAtDesc(UUID vehicleId);

    List<OperationalAuthorizationPersistenceEntity> findAllByOverrideTrueAndAuthorizedAtBetweenOrderByAuthorizedAtDesc(Instant from, Instant to);

    @Query("""
            select authorization from OperationalAuthorizationPersistenceEntity authorization
            where authorization.authorizedAt = (
                select max(latest.authorizedAt) from OperationalAuthorizationPersistenceEntity latest
                where latest.vehicleId = authorization.vehicleId)
            order by authorization.authorizedAt desc""")
    List<OperationalAuthorizationPersistenceEntity> findLatestOfEachVehicle();
}
