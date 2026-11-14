package com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.LicenseNumber;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.UserId;

import java.time.LocalDate;

/**
 * Registers the driver data of an existing user account of the company of the signed-in user:
 * the number of the driving license and the day it expires.
 */
public record RegisterDriverCommand(
        CompanyId companyId,
        UserId userId,
        LicenseNumber licenseNumber,
        LocalDate licenseExpirationDate) {

    public RegisterDriverCommand {
        if (companyId == null) throw new IllegalArgumentException("fleet.error.company-id.required");
        if (userId == null) throw new IllegalArgumentException("fleet.driver.error.user-id.required");
        if (licenseNumber == null) throw new IllegalArgumentException("fleet.driver.error.license-number.required");
        if (licenseExpirationDate == null) throw new IllegalArgumentException("fleet.driver.error.license-expiration.required");
    }
}
