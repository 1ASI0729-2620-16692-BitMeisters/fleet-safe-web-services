package com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects;

import java.util.UUID;

/**
 * Identifier of a company. It is the same one the users of the company carry in their token.
 */
public record CompanyId(UUID value) {

    public CompanyId {
        if (value == null) {
            throw new IllegalArgumentException("fleet.error.company-id.required");
        }
    }
}
