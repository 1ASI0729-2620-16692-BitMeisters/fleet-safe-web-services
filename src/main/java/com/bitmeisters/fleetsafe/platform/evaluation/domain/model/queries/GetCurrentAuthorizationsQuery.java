package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.AuthorizationStatus;

import java.util.Optional;

public record GetCurrentAuthorizationsQuery(Optional<AuthorizationStatus> status) {

    public GetCurrentAuthorizationsQuery {
        status = status == null ? Optional.empty() : status;
    }
}
