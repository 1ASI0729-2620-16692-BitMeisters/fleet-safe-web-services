package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Fleet;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.FleetId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.FleetRepository;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.assemblers.FleetPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.repositories.FleetPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FleetRepositoryImpl implements FleetRepository {

    private final FleetPersistenceRepository fleetPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public FleetRepositoryImpl(FleetPersistenceRepository fleetPersistenceRepository,
                               ApplicationEventPublisher eventPublisher) {
        this.fleetPersistenceRepository = fleetPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Fleet> findAllByCompanyId(CompanyId companyId) {
        return fleetPersistenceRepository.findAllByCompanyIdOrderByNameAsc(companyId.value()).stream()
                .map(FleetPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsByIdAndCompanyId(FleetId id, CompanyId companyId) {
        return fleetPersistenceRepository.existsByIdAndCompanyId(id.value(), companyId.value());
    }

    @Override
    public Fleet save(Fleet fleet) {
        var events = List.copyOf(fleet.domainEvents());
        var saved = fleetPersistenceRepository.save(FleetPersistenceAssembler.toPersistenceFromDomain(fleet));
        events.forEach(eventPublisher::publishEvent);
        fleet.clearDomainEvents();
        return FleetPersistenceAssembler.toDomainFromPersistence(saved);
    }
}
