package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.IncidentType;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.IncidentTypeRepository;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.assemblers.IncidentTypePersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.repositories.IncidentTypePersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class IncidentTypeRepositoryImpl implements IncidentTypeRepository {

    private final IncidentTypePersistenceRepository persistenceRepository;

    public IncidentTypeRepositoryImpl(IncidentTypePersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public Optional<IncidentType> findById(UUID id) {
        return persistenceRepository.findById(id)
                .map(IncidentTypePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<IncidentType> findAll() {
        return persistenceRepository.findAll().stream()
                .map(IncidentTypePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<IncidentType> findAllByActiveTrue() {
        return persistenceRepository.findAllByActiveTrue().stream()
                .map(IncidentTypePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsByCode(String code) {
        return persistenceRepository.existsByCode(code);
    }

    @Override
    public IncidentType save(IncidentType incidentType) {
        var entity = IncidentTypePersistenceAssembler
                .toPersistenceFromDomain(incidentType);
        var saved = persistenceRepository.save(entity);
        return IncidentTypePersistenceAssembler.toDomainFromPersistence(saved);
    }
}