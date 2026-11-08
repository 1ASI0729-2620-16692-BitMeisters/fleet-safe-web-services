package com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries;

public record GetExpiringDocumentsQuery(int days) {

    public static final int MAX_DAYS = 365;

    public GetExpiringDocumentsQuery {
        if (days < 0 || days > MAX_DAYS) throw new IllegalArgumentException("documentation.document.error.days.range");
    }
}
