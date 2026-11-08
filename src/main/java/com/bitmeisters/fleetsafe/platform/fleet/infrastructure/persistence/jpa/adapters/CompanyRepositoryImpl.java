package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Company;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.TaxId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.CompanyRepository;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.assemblers.CompanyPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.repositories.CompanyPersistenceRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CompanyRepositoryImpl implements CompanyRepository {

    private final CompanyPersistenceRepository companyPersistenceRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CompanyRepositoryImpl(CompanyPersistenceRepository companyPersistenceRepository,
                                 ApplicationEventPublisher eventPublisher) {
        this.companyPersistenceRepository = companyPersistenceRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Company> findById(CompanyId id) {
        return companyPersistenceRepository.findById(id.value()).map(CompanyPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsById(CompanyId id) {
        return companyPersistenceRepository.existsById(id.value());
    }

    @Override
    public boolean existsByTaxId(TaxId taxId) {
        return companyPersistenceRepository.existsByTaxId(taxId.value());
    }

    @Override
    public Company save(Company company) {
        var events = List.copyOf(company.domainEvents());
        var saved = companyPersistenceRepository.save(CompanyPersistenceAssembler.toPersistenceFromDomain(company));
        events.forEach(eventPublisher::publishEvent);
        company.clearDomainEvents();
        return CompanyPersistenceAssembler.toDomainFromPersistence(saved);
    }
}
