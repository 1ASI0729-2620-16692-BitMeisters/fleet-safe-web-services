package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects;

public enum AuthorizationStatus {
    ENABLED,
    OBSERVED,
    NOT_ENABLED;

    public static AuthorizationStatus fromImpact(RuleImpact impact) {
        return switch (impact) {
            case NONE -> ENABLED;
            case OBSERVED -> OBSERVED;
            case BLOCKING -> NOT_ENABLED;
        };
    }
}
