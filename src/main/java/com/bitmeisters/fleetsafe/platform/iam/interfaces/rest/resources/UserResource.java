package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Response resource representing a user.")
public record UserResource(
        @Schema(description = "Persistent identifier", example = "7b1f6a2e-3c4d-4e5f-8a9b-0c1d2e3f4a5b")
        UUID id,
        @Schema(description = "Company the user belongs to", example = "1a2b3c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d")
        UUID companyId,
        @Schema(description = "Email of the user", example = "supervisor@transportesandinos.pe")
        String email,
        @Schema(description = "First name", example = "Rosa")
        String firstName,
        @Schema(description = "Last name", example = "Quispe")
        String lastName,
        @Schema(description = "Role of the user", example = "FLEET_SUPERVISOR",
                allowableValues = {"ADMINISTRATOR", "FLEET_SUPERVISOR", "DRIVER"})
        String role,
        @Schema(description = "Whether the user can sign in", example = "true")
        boolean isActive,
        @Schema(description = "When the user was registered", example = "2026-09-29T14:00:00Z")
        Instant createdAt,
        @Schema(description = "When the user was last updated", example = "2026-09-29T14:00:00Z")
        Instant updatedAt) {}
