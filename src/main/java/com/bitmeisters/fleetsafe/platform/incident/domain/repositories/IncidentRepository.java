package com.bitmeisters.fleetsafe.platform.incident.domain.repositories;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.Incident;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidentRepository {

    Optional<Incident> findById(UUID id);

    List<Incident> findAll();

    List<Incident> findAllByVehicleId(UUID vehicleId);

    List<Incident> findAllByStatus(
            com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentStatus status
    );

    boolean existsById(UUID id);

    Incident save(Incident incident);

    void delete(Incident incident);
}