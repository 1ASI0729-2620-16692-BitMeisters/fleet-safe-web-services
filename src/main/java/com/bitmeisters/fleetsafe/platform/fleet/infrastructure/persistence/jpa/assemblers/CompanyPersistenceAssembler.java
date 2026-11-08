package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Company;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyName;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.TaxId;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities.CompanyPersistenceEntity;

public final class CompanyPersistenceAssembler {

    private CompanyPersistenceAssembler() {
    }

    public static Company toDomainFromPersistence(CompanyPersistenceEntity entity) {
        if (entity == null) return null;
        return Company.restore(
                entity.getId(),
                new CompanyName(entity.getName()),
                new TaxId(entity.getTaxId()),
                entity.getAddress(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static CompanyPersistenceEntity toPersistenceFromDomain(Company company) {
        if (company == null) return null;
        var entity = new CompanyPersistenceEntity();
        entity.setId(company.getId());
        entity.setCreatedAt(company.getCreatedAt());
        entity.setName(company.getName().value());
        entity.setTaxId(company.getTaxId().value());
        entity.setAddress(company.getAddress());
        entity.setPhone(company.getPhone());
        entity.setEmail(company.getEmail());
        return entity;
    }
}
