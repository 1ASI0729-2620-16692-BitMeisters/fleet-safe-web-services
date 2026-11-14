package com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects;

import java.util.UUID;

/**
 * Identifier of the user account of a driver. The account belongs to Identity and Access, so Fleet
 * Management only keeps its identifier.
 */
public record UserId(UUID value) {

    public UserId {
        if (value == null) {
            throw new IllegalArgumentException("fleet.driver.error.user-id.required");
        }
    }
}
