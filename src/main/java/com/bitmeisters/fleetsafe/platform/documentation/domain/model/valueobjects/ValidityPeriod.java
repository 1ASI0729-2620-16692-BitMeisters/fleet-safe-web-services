package com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects;

import java.time.LocalDate;

public record ValidityPeriod(LocalDate issueDate, LocalDate expirationDate) {

    public ValidityPeriod {
        if (issueDate == null) {
            throw new IllegalArgumentException("documentation.document.error.issue-date.required");
        }
        if (expirationDate == null) {
            throw new IllegalArgumentException("documentation.document.error.expiration-date.required");
        }
        if (expirationDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("documentation.document.error.period.order");
        }
    }
}
