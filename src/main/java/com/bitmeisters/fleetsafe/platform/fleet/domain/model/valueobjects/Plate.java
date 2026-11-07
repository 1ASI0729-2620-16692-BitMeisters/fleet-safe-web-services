package com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects;

import java.util.regex.Pattern;

/**
 * License plate of a vehicle, written as two groups of three letters or digits, e.g. ABC-123.
 * It is kept in upper case, so ABC-123 and abc-123 are the same plate.
 */
public record Plate(String value) {

    public static final int MAX_LENGTH = 10;

    private static final Pattern FORMAT = Pattern.compile("^[A-Z0-9]{3}-[A-Z0-9]{3}$");

    public Plate {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("fleet.vehicle.error.plate.required");
        }
        value = value.strip().toUpperCase();
        if (!FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("fleet.vehicle.error.plate.pattern");
        }
    }
}
