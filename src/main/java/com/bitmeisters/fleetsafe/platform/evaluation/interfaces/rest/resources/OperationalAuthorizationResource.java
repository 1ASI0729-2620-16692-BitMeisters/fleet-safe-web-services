package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Response resource representing the operational authorization of a vehicle.")
public record OperationalAuthorizationResource(
        @Schema(description = "Persistent identifier", example = "7c1e2d3f-4a5b-4c6d-8e9f-0a1b2c3d4e5f")
        UUID id,
        @Schema(description = "Evaluation that produced the authorization", example = "2b4d6f8a-1c3e-4a5b-9c7d-6e5f4a3b2c1d")
        UUID evaluationId,
        @Schema(description = "Authorized vehicle", example = "9a8b7c6d-5e4f-4a3b-8c2d-1e0f9a8b7c6d")
        UUID vehicleId,
        @Schema(description = "Operational status of the vehicle", example = "NOT_ENABLED",
                allowableValues = {"ENABLED", "OBSERVED", "NOT_ENABLED"})
        String status,
        @Schema(description = "Whether the authorization lifts a block", example = "false")
        boolean isOverride,
        @Schema(description = "Reason for lifting the block, only when it is an override", example = "null")
        String overrideReason,
        @Schema(description = "User who authorized, empty when the evaluation was automatic", example = "null")
        UUID authorizedBy,
        @Schema(description = "When the authorization was issued", example = "2026-10-26T14:30:00Z")
        Instant authorizedAt,
        @Schema(description = "End of validity, empty when it has no expiry", example = "null")
        Instant validUntil) {}
