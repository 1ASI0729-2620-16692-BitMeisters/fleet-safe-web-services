package com.bitmeisters.fleetsafe.platform.iam.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;

public record GetUserByEmailQuery(EmailAddress email) {

    public GetUserByEmailQuery {
        if (email == null) throw new IllegalArgumentException("iam.user.error.email.required");
    }
}
