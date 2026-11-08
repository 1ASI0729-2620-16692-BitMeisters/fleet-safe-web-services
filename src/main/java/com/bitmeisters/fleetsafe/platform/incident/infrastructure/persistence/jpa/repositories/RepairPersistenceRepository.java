package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.entities.RepairPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RepairPersistenceRepository
        extends JpaRepository<RepairPersistenceEntity, UUID> {

    List<RepairPersistenceEntity> findAllByIncidentId(UUID incidentId);
}