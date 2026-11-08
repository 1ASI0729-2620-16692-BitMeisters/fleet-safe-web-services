package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries;

import java.time.Instant;

public record GetOverridesByPeriodQuery(Instant from, Instant to) {

    public GetOverridesByPeriodQuery {
        if (from == null || to == null) throw new IllegalArgumentException("evaluation.authorization.error.period.required");
        if (from.isAfter(to)) throw new IllegalArgumentException("evaluation.authorization.error.period.order");
    }
}
