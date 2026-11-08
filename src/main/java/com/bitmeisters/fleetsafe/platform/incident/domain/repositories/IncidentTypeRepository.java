package com.bitmeisters.fleetsafe.platform.incident.domain.repositories;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.IncidentType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidentTypeRepository {

    Optional<IncidentType> findById(UUID id);

    List<IncidentType> findAll();

    List<IncidentType> findAllByActiveTrue();

    boolean existsByCode(String code);

    IncidentType save(IncidentType incidentType);
}