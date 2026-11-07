package com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects;

import java.time.Year;

/**
 * Year a vehicle was manufactured: from 1950 up to next year, since models are sold ahead of time.
 */
public record ManufacturingYear(int value) {

    public static final int MIN_YEAR = 1950;

    public ManufacturingYear {
        if (value < MIN_YEAR || value > Year.now().getValue() + 1) {
            throw new IllegalArgumentException("fleet.vehicle.error.year.range");
        }
    }
}
