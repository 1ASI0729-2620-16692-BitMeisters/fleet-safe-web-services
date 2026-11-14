package com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects;

/**
 * Number of the driving license of a driver. It is kept in upper case, so q12345678 and Q12345678
 * are the same license.
 */
public record LicenseNumber(String value) {

    public static final int MAX_LENGTH = 20;

    public LicenseNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("fleet.driver.error.license-number.required");
        }
        value = value.strip().toUpperCase();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("fleet.driver.error.license-number.size");
        }
    }
}
