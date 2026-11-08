package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Schema(description = "Response resource representing a fleet.")
public record FleetResource(
        @Schema(description = "Persistent identifier", example = "3f2b9c6e-1a4d-4e8b-9c2a-7d5e6f8a9b01")
        UUID id,
        @Schema(description = "Company that owns the fleet", example = "7c1d2e3f-4a5b-4c6d-8e9f-0a1b2c3d4e5f")
        UUID companyId,
        @Schema(description = "Name of the fleet", example = "Flota Lima")
        String name,
        @Schema(description = "Description of the fleet", example = "Flota principal de operaciones de Lima")
        String description,
        @Schema(description = "Moment the fleet was created", example = "2026-10-01T10:30:00Z")
        Instant createdAt) {}
