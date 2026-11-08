package com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.tokens;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;

public interface TokenService {

    String generateToken(User user);

    long getExpirationInSeconds();

    String getUserIdFromToken(String token);

    boolean validateToken(String token);
}
