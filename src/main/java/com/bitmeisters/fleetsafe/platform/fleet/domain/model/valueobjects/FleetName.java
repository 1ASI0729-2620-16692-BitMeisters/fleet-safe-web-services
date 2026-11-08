package com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects;

public record FleetName(String value) {

    public static final int MAX_LENGTH = 120;

    public FleetName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("fleet.error.name.required");
        }
        value = value.strip();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("fleet.error.name.size");
        }
    }
}
