package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects;

public record ObservationDescription(String value) {

    public static final int MAX_LENGTH = 1000;

    public ObservationDescription {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("inspection.observation.error.description.required");
        }
        value = value.strip();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("inspection.observation.error.description.size");
        }
    }
}
