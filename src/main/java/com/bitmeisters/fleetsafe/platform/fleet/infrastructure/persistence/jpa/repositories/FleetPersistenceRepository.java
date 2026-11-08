package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities.FleetPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FleetPersistenceRepository extends JpaRepository<FleetPersistenceEntity, UUID> {

    List<FleetPersistenceEntity> findAllByCompanyIdOrderByNameAsc(UUID companyId);

    boolean existsByIdAndCompanyId(UUID id, UUID companyId);
}
