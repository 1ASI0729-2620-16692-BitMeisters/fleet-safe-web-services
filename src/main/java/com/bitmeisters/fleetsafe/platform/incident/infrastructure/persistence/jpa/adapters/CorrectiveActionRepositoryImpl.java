package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.CorrectiveAction;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.CorrectiveActionRepository;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.assemblers.CorrectiveActionPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.repositories.CorrectiveActionPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class CorrectiveActionRepositoryImpl implements CorrectiveActionRepository {

    private final CorrectiveActionPersistenceRepository persistenceRepository;

    public CorrectiveActionRepositoryImpl(
            CorrectiveActionPersistenceRepository persistenceRepository) {
        this.persistenceRepository = persistenceRepository;
    }

    @Override
    public List<CorrectiveAction> findAllByIncidentId(UUID incidentId) {
        return persistenceRepository.findAllByIncidentId(incidentId).stream()
                .map(CorrectiveActionPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public CorrectiveAction save(CorrectiveAction correctiveAction) {
        var entity = CorrectiveActionPersistenceAssembler
                .toPersistenceFromDomain(correctiveAction);
        var saved = persistenceRepository.save(entity);
        return CorrectiveActionPersistenceAssembler.toDomainFromPersistence(saved);
    }
}