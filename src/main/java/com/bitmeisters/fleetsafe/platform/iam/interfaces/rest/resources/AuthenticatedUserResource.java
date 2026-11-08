package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response resource returned after signing in.")
public record AuthenticatedUserResource(
        @Schema(description = "JWT to send as Authorization: Bearer <token>", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIn0.signature")
        String accessToken,
        @Schema(description = "Token type", example = "Bearer")
        String tokenType,
        @Schema(description = "Seconds until the token expires", example = "86400")
        long expiresIn,
        @Schema(description = "The signed-in user")
        UserResource user) {}
