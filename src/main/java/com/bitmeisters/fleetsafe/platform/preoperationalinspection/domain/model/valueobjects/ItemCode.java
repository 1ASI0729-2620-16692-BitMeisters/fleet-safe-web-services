package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects;

public record ItemCode(String value) {
    public static final int MAX_LENGTH = 30;
    public ItemCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("inspection.item.error.code.required");
        }
        value = value.strip().toUpperCase();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("inspection.item.error.code.size");
        }
    }
}
