package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities.CompanyPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompanyPersistenceRepository extends JpaRepository<CompanyPersistenceEntity, UUID> {

    boolean existsByTaxId(String taxId);
}
