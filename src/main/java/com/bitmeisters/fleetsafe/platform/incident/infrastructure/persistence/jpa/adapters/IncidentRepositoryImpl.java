package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.Incident;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentStatus;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.IncidentRepository;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.assemblers.IncidentPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.repositories.IncidentPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class IncidentRepositoryImpl implements IncidentRepository {

    private final IncidentPersistenceRepository persistenceRepository;
    private final IncidentPersistenceAssembler assembler;

    public IncidentRepositoryImpl(
            IncidentPersistenceRepository persistenceRepository,
            IncidentPersistenceAssembler assembler) {
        this.persistenceRepository = persistenceRepository;
        this.assembler = assembler;
    }

    @Override
    public Optional<Incident> findById(UUID id) {
        return persistenceRepository.findById(id)
                .map(assembler::toDomainFromPersistence);
    }

    @Override
    public List<Incident> findAll() {
        return persistenceRepository.findAll().stream()
                .map(assembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Incident> findAllByVehicleId(UUID vehicleId) {
        return persistenceRepository.findAllByVehicleId(vehicleId).stream()
                .map(assembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Incident> findAllByStatus(IncidentStatus status) {
        return persistenceRepository.findAllByStatus(status).stream()
                .map(assembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return persistenceRepository.existsById(id);
    }

    @Override
    public Incident save(Incident incident) {
        var entity = assembler.toPersistenceFromDomain(incident);
        var saved = persistenceRepository.save(entity);
        return assembler.toDomainFromPersistence(saved);
    }

    @Override
    public void delete(Incident incident) {
        if (incident != null && incident.getId() != null) {
            persistenceRepository.deleteById(incident.getId());
        }
    }
}