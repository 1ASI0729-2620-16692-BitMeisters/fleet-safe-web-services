package com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects;

public record DocumentTypeName(String value) {

    public static final int MAX_LENGTH = 120;

    public DocumentTypeName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("documentation.type.error.name.required");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("documentation.type.error.name.size");
        }
    }
}
