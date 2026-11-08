package com.bitmeisters.fleetsafe.platform.iam.domain.model.queries;

import java.util.UUID;

public record GetUserByIdQuery(UUID userId) {

    public GetUserByIdQuery {
        if (userId == null) throw new IllegalArgumentException("iam.user.error.id.required");
    }
}
