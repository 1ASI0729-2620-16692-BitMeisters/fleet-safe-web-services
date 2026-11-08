package com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects;

public record DocumentNumber(String value) {

    public static final int MAX_LENGTH = 60;

    public DocumentNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("documentation.document.error.number.required");
        }
        value = value.strip();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("documentation.document.error.number.size");
        }
    }
}
