package com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.RegisterDriverCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.events.DriverRegisteredEvent;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.LicenseNumber;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.UserId;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * A driver of a company: the data of the person that does not belong to the user account, which
 * Identity and Access keeps, but to the activity of driving. The account is referenced by identifier.
 */
@Getter
public class Driver extends AbstractDomainAggregateRoot<Driver> {

    private UUID id;
    private CompanyId companyId;
    private UserId userId;
    private LicenseNumber licenseNumber;
    private LocalDate licenseExpirationDate;
    private Instant createdAt;
    private Instant updatedAt;

    private Driver() {
    }

    public Driver(RegisterDriverCommand command) {
        this.companyId = command.companyId();
        this.userId = command.userId();
        this.licenseNumber = command.licenseNumber();
        this.licenseExpirationDate = command.licenseExpirationDate();
        registerDomainEvent(new DriverRegisteredEvent(command.companyId(), command.userId()));
    }

    public static Driver restore(UUID id, CompanyId companyId, UserId userId, LicenseNumber licenseNumber,
                                 LocalDate licenseExpirationDate, Instant createdAt, Instant updatedAt) {
        var driver = new Driver();
        driver.id = id;
        driver.companyId = companyId;
        driver.userId = userId;
        driver.licenseNumber = licenseNumber;
        driver.licenseExpirationDate = licenseExpirationDate;
        driver.createdAt = createdAt;
        driver.updatedAt = updatedAt;
        return driver;
    }

    /**
     * The license is in force on the day it expires and on every day before it.
     */
    public boolean hasValidLicenseOn(LocalDate day) {
        return !licenseExpirationDate.isBefore(day);
    }
}
