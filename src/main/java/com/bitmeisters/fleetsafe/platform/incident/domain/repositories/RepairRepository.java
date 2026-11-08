package com.bitmeisters.fleetsafe.platform.incident.domain.repositories;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.Repair;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RepairRepository {

    Optional<Repair> findById(UUID id);

    List<Repair> findAllByIncidentId(UUID incidentId);

    Repair save(Repair repair);
}