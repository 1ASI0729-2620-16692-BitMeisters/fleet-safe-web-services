package com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects;

import java.util.UUID;

public record CompanyId(UUID value) {

    public CompanyId {
        if (value == null) {
            throw new IllegalArgumentException("iam.user.error.company-id.required");
        }
    }
}
