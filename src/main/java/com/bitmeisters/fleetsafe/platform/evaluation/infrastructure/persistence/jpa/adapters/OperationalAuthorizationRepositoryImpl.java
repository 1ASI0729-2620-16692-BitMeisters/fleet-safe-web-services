package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.OperationalAuthorizationRepository;
import com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.assemblers.OperationalAuthorizationPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.repositories.OperationalAuthorizationPersistenceRepository;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class OperationalAuthorizationRepositoryImpl implements OperationalAuthorizationRepository {

    private final OperationalAuthorizationPersistenceRepository operationalAuthorizationPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public OperationalAuthorizationRepositoryImpl(OperationalAuthorizationPersistenceRepository operationalAuthorizationPersistenceRepository,
                                                  DomainEventPublisher domainEventPublisher) {
        this.operationalAuthorizationPersistenceRepository = operationalAuthorizationPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<OperationalAuthorization> findFirstByVehicleIdOrderByAuthorizedAtDesc(VehicleId vehicleId) {
        return operationalAuthorizationPersistenceRepository.findFirstByVehicleIdOrderByAuthorizedAtDesc(vehicleId.value())
                .map(OperationalAuthorizationPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<OperationalAuthorization> findAllByVehicleIdOrderByAuthorizedAtDesc(VehicleId vehicleId) {
        return operationalAuthorizationPersistenceRepository.findAllByVehicleIdOrderByAuthorizedAtDesc(vehicleId.value()).stream()
                .map(OperationalAuthorizationPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<OperationalAuthorization> findAllByOverrideTrueAndAuthorizedAtBetweenOrderByAuthorizedAtDesc(Instant from, Instant to) {
        return operationalAuthorizationPersistenceRepository.findAllByOverrideTrueAndAuthorizedAtBetweenOrderByAuthorizedAtDesc(from, to).stream()
                .map(OperationalAuthorizationPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public OperationalAuthorization save(OperationalAuthorization authorization) {
        var saved = operationalAuthorizationPersistenceRepository.save(
                OperationalAuthorizationPersistenceAssembler.toPersistenceFromDomain(authorization));
        domainEventPublisher.publishAndClearEvents(authorization);
        return OperationalAuthorizationPersistenceAssembler.toDomainFromPersistence(saved);
    }
}
