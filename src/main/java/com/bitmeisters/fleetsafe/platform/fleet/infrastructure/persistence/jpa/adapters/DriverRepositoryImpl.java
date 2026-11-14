package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Driver;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.UserId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.DriverRepository;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.assemblers.DriverPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.repositories.DriverPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class DriverRepositoryImpl implements DriverRepository {

    private final DriverPersistenceRepository driverPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DriverRepositoryImpl(DriverPersistenceRepository driverPersistenceRepository,
                                ApplicationEventPublisher eventPublisher) {
        this.driverPersistenceRepository = driverPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Driver> findByIdAndCompanyId(DriverId id, CompanyId companyId) {
        return driverPersistenceRepository.findByIdAndCompanyId(id.value(), companyId.value())
                .map(DriverPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Driver> findByUserIdAndCompanyId(UserId userId, CompanyId companyId) {
        return driverPersistenceRepository.findByUserIdAndCompanyId(userId.value(), companyId.value())
                .map(DriverPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Driver> findAllByCompanyId(CompanyId companyId) {
        return driverPersistenceRepository.findAllByCompanyIdOrderByCreatedAtAsc(companyId.value()).stream()
                .map(DriverPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        return driverPersistenceRepository.existsByUserId(userId.value());
    }

    @Override
    public Driver save(Driver driver) {
        var events = List.copyOf(driver.domainEvents());
        var saved = driverPersistenceRepository.saveAndFlush(DriverPersistenceAssembler.toPersistenceFromDomain(driver));
        events.forEach(eventPublisher::publishEvent);
        driver.clearDomainEvents();
        return DriverPersistenceAssembler.toDomainFromPersistence(saved);
    }
}
