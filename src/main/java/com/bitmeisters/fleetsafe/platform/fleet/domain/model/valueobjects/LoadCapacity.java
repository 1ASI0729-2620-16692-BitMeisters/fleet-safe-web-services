package com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Load capacity of a vehicle, kept with two decimals so that what is stored is what is returned.
 */
public record LoadCapacity(BigDecimal value) {

    public static final int SCALE = 2;
    public static final int PRECISION = 10;
    public static final BigDecimal MAX_VALUE = new BigDecimal("99999999.99");

    public LoadCapacity {
        if (value == null) {
            throw new IllegalArgumentException("fleet.vehicle.error.capacity.required");
        }
        value = value.setScale(SCALE, RoundingMode.HALF_UP);
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("fleet.vehicle.error.capacity.positive");
        }
        if (value.compareTo(MAX_VALUE) > 0) {
            throw new IllegalArgumentException("fleet.vehicle.error.capacity.max");
        }
    }
}
