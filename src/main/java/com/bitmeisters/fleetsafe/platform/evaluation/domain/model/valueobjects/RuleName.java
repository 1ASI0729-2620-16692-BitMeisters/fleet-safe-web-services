package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects;

public record RuleName(String value) {

    public static final int MAX_LENGTH = 120;

    public RuleName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("evaluation.rule.error.name.required");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("evaluation.rule.error.name.size");
        }
    }
}
