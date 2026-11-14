package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Response resource representing a driver.")
public record DriverResource(
        @Schema(description = "Persistent identifier", example = "7b8c9d0e-1f2a-4b3c-8d4e-5f6a7b8c9d0e")
        UUID id,
        @Schema(description = "User account of the driver", example = "2c3d4e5f-6a7b-4c8d-9e0f-1a2b3c4d5e6f")
        UUID userId,
        @Schema(description = "Number of the driving license", example = "Q12345678")
        String licenseNumber,
        @Schema(description = "Day the driving license expires", example = "2028-05-30")
        LocalDate licenseExpirationDate,
        @Schema(description = "Moment the driver was registered", example = "2026-10-01T11:00:00Z")
        Instant createdAt,
        @Schema(description = "Moment the driver was last modified", example = "2026-10-01T11:00:00Z")
        Instant updatedAt) {}
