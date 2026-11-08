package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.entities.IncidentFollowUpPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IncidentFollowUpPersistenceRepository
        extends JpaRepository<IncidentFollowUpPersistenceEntity, UUID> {

    List<IncidentFollowUpPersistenceEntity> findAllByIncidentIdOrderByCreatedAtDesc(
            UUID incidentId);
}