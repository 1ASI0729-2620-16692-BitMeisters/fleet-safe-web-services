package com.bitmeisters.fleetsafe.platform.iam.infrastructure.tokens.jwt;

import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.tokens.TokenService;
import jakarta.servlet.http.HttpServletRequest;

public interface BearerTokenService extends TokenService {

    String getBearerTokenFrom(HttpServletRequest request);
}
