package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities.DriverPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverPersistenceRepository extends JpaRepository<DriverPersistenceEntity, UUID> {

    boolean existsByUserId(UUID userId);

    Optional<DriverPersistenceEntity> findByIdAndCompanyId(UUID id, UUID companyId);

    Optional<DriverPersistenceEntity> findByUserIdAndCompanyId(UUID userId, UUID companyId);

    List<DriverPersistenceEntity> findAllByCompanyIdOrderByCreatedAtAsc(UUID companyId);
}
