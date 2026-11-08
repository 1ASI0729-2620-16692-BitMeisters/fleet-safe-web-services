package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects;

public enum RuleImpact {
    NONE,
    OBSERVED,
    BLOCKING;

    public RuleImpact mostRestrictive(RuleImpact other) {
        return other.ordinal() > this.ordinal() ? other : this;
    }
}
