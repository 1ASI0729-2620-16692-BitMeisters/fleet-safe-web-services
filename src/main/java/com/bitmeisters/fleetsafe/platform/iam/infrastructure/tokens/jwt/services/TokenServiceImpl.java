package com.bitmeisters.fleetsafe.platform.iam.infrastructure.tokens.jwt.services;

import com.bitmeisters.fleetsafe.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.valueobjects.Timestamps;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
public class TokenServiceImpl implements BearerTokenService {

    private static final String AUTHORIZATION_PARAMETER_NAME = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private final SecretKey signingKey;
    private final long expirationInSeconds;

    public TokenServiceImpl(@Value("${authorization.jwt.secret}") String secret,
                            @Value("${authorization.jwt.expiration.seconds}") long expirationInSeconds) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationInSeconds = expirationInSeconds;
    }

    @Override
    public String generateToken(User user) {
        var issuedAt = Timestamps.now();
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .claim("companyId", user.getCompanyId().value().toString())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plusSeconds(expirationInSeconds)))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public long getExpirationInSeconds() {
        return expirationInSeconds;
    }

    @Override
    public String getUserIdFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.debug("JSON Web Token is expired: {}", e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JSON Web Token: {}", e.getMessage());
        }
        return false;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }

    @Override
    public String getBearerTokenFrom(HttpServletRequest request) {
        var authorizationParameter = request.getHeader(AUTHORIZATION_PARAMETER_NAME);
        if (StringUtils.hasText(authorizationParameter) && authorizationParameter.startsWith(BEARER_TOKEN_PREFIX)) {
            return authorizationParameter.substring(BEARER_TOKEN_PREFIX.length());
        }
        return null;
    }
}
