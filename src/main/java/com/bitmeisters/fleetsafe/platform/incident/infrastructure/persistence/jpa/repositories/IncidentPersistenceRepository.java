package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.repositories;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentStatus;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.entities.IncidentPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IncidentPersistenceRepository
        extends JpaRepository<IncidentPersistenceEntity, UUID> {

    List<IncidentPersistenceEntity> findAllByVehicleId(UUID vehicleId);

    List<IncidentPersistenceEntity> findAllByStatus(IncidentStatus status);
}