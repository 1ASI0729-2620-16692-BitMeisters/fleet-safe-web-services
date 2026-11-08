package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.Repair;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.RepairRepository;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.assemblers.RepairPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.repositories.RepairPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RepairRepositoryImpl implements RepairRepository {

    private final RepairPersistenceRepository persistenceRepository;

    public RepairRepositoryImpl(RepairPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Optional<Repair> findById(UUID id) {
        return persistenceRepository.findById(id)
                .map(RepairPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Repair> findAllByIncidentId(UUID incidentId) {
        return persistenceRepository.findAllByIncidentId(incidentId).stream()
                .map(RepairPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Repair save(Repair repair) {
        var entity = RepairPersistenceAssembler.toPersistenceFromDomain(repair);
        var saved = persistenceRepository.save(entity);
        return RepairPersistenceAssembler.toDomainFromPersistence(saved);
    }
}