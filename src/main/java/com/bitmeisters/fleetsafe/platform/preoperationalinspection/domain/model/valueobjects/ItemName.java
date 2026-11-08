package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects;

public record ItemName(String value) {
    public static final int MAX_LENGTH = 120;
    public ItemName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("inspection.item.error.name.required");
        }
        value = value.strip();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("inspection.item.error.name.size");
        }
    }
}
