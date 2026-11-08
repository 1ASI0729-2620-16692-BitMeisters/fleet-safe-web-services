package com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects;

public record VehicleModel(String value) {

    public static final int MAX_LENGTH = 60;

    public VehicleModel {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("fleet.vehicle.error.model.required");
        }
        value = value.strip();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("fleet.vehicle.error.model.size");
        }
    }
}
