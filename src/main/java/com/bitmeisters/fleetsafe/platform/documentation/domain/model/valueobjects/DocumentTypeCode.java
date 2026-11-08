package com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects;

public record DocumentTypeCode(String value) {

    public static final int MAX_LENGTH = 30;

    public DocumentTypeCode {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("documentation.type.error.code.required");
        }
        value = value.strip().toUpperCase();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("documentation.type.error.code.size");
        }
    }
}
