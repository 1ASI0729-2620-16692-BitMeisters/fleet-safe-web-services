package com.bitmeisters.fleetsafe.platform.iam.infrastructure.tokens.jwt.services;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TokenServiceImplTest {

    private static final String SECRET = "test-secret-with-at-least-thirty-two-bytes-for-hmac-sha";

    private final TokenServiceImpl tokenService = new TokenServiceImpl(SECRET, 3600);
    private final User supervisor = User.restore(UUID.randomUUID(), new CompanyId(UUID.randomUUID()),
            new EmailAddress("rosa@andinos.pe"), "$2a$10$hash", new PersonName("Rosa", "Quispe"),
            Role.FLEET_SUPERVISOR, true, Instant.now(), Instant.now());

    @Test
    void identifiesTheUserItWasIssuedFor() {
        var token = tokenService.generateToken(supervisor);

        assertThat(tokenService.validateToken(token)).isTrue();
        assertThat(tokenService.getUserIdFromToken(token)).isEqualTo(supervisor.getId().toString());
    }

    @Test
    void carriesTheRoleAndTheCompanyOfTheUser() {
        var claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8))).build()
                .parseSignedClaims(tokenService.generateToken(supervisor)).getPayload();

        assertThat(claims.get("role", String.class)).isEqualTo("FLEET_SUPERVISOR");
        assertThat(claims.get("companyId", String.class)).isEqualTo(supervisor.getCompanyId().value().toString());
        assertThat(claims.getExpiration().toInstant()).isEqualTo(claims.getIssuedAt().toInstant().plusSeconds(3600));
    }

    @Test
    void rejectsATokenWhoseContentWasAltered() {
        var parts = tokenService.generateToken(supervisor).split("\\.");
        var forged = parts[0] + "." + parts[1].substring(0, parts[1].length() - 2) + "AA." + parts[2];

        assertThat(tokenService.validateToken(forged)).isFalse();
    }

    @Test
    void rejectsATokenSignedWithAnotherSecret() {
        var other = new TokenServiceImpl("another-secret-with-at-least-thirty-two-bytes-too", 3600);

        assertThat(tokenService.validateToken(other.generateToken(supervisor))).isFalse();
    }

    @Test
    void rejectsAnExpiredToken() {
        var expiring = new TokenServiceImpl(SECRET, -1);

        assertThat(expiring.validateToken(expiring.generateToken(supervisor))).isFalse();
    }
}
