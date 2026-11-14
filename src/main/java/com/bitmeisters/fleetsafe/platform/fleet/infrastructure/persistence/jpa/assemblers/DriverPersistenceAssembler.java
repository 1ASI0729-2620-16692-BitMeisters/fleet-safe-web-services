package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Driver;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.LicenseNumber;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.UserId;
import com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities.DriverPersistenceEntity;

public final class DriverPersistenceAssembler {

    private DriverPersistenceAssembler() {
    }

    public static Driver toDomainFromPersistence(DriverPersistenceEntity entity) {
        if (entity == null) return null;
        return Driver.restore(
                entity.getId(),
                new CompanyId(entity.getCompanyId()),
                new UserId(entity.getUserId()),
                new LicenseNumber(entity.getLicenseNumber()),
                entity.getLicenseExpirationDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static DriverPersistenceEntity toPersistenceFromDomain(Driver driver) {
        if (driver == null) return null;
        var entity = new DriverPersistenceEntity();
        if (driver.getId() != null) {
            entity.setId(driver.getId());
            entity.setCreatedAt(driver.getCreatedAt());
        }
        entity.setCompanyId(driver.getCompanyId().value());
        entity.setUserId(driver.getUserId().value());
        entity.setLicenseNumber(driver.getLicenseNumber().value());
        entity.setLicenseExpirationDate(driver.getLicenseExpirationDate());
        return entity;
    }
}
