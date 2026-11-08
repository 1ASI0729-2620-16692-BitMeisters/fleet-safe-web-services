package com.bitmeisters.fleetsafe.platform.iam.application.commandservices;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;

public record AuthenticatedUser(User user, String token, long expiresInSeconds) {
}
