package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.IncidentFollowUp;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.IncidentFollowUpRepository;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.assemblers.IncidentFollowUpPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.repositories.IncidentFollowUpPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class IncidentFollowUpRepositoryImpl implements IncidentFollowUpRepository {

    private final IncidentFollowUpPersistenceRepository persistenceRepository;

    public IncidentFollowUpRepositoryImpl(
            IncidentFollowUpPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public List<IncidentFollowUp> findAllByIncidentIdOrderByCreatedAtDesc(UUID incidentId) {
        return persistenceRepository
                .findAllByIncidentIdOrderByCreatedAtDesc(incidentId).stream()
                .map(IncidentFollowUpPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public IncidentFollowUp save(IncidentFollowUp followUp) {
        var entity = IncidentFollowUpPersistenceAssembler
                .toPersistenceFromDomain(followUp);
        var saved = persistenceRepository.save(entity);
        return IncidentFollowUpPersistenceAssembler.toDomainFromPersistence(saved);
    }
}