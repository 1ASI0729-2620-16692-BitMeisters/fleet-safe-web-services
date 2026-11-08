package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects;

public record Odometer(Integer value) {
    public Odometer {
        if (value == null || value < 0) {
            throw new IllegalArgumentException("inspection.error.odometer.invalid");
        }
    }
}
