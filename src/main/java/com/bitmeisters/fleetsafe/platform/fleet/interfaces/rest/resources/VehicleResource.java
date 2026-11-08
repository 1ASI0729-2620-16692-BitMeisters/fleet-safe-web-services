package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Response resource representing a vehicle.")
public record VehicleResource(
        @Schema(description = "Persistent identifier", example = "4d5e6f7a-8b9c-4d0e-af1b-2c3d4e5f6a7b")
        UUID id,
        @Schema(description = "Fleet the vehicle belongs to", example = "3f2b9c6e-1a4d-4e8b-9c2a-7d5e6f8a9b01")
        UUID fleetId,
        @Schema(description = "License plate", example = "ABC-123")
        String plate,
        @Schema(description = "Brand of the vehicle", example = "Volvo")
        String brand,
        @Schema(description = "Model of the vehicle", example = "FH 460")
        String model,
        @Schema(description = "Manufacturing year", example = "2024")
        int year,
        @Schema(description = "Size category of the vehicle", example = "LARGE", allowableValues = {"THIN", "MEDIUM", "LARGE"})
        String type,
        @Schema(description = "Load capacity of the vehicle", example = "20.00")
        BigDecimal capacity,
        @Schema(description = "Current operational condition, derived from the latest authorization", example = "ENABLED",
                allowableValues = {"ENABLED", "OBSERVED", "NOT_ENABLED"})
        String currentStatus,
        @Schema(description = "Moment the vehicle was registered", example = "2026-10-01T11:00:00Z")
        Instant createdAt,
        @Schema(description = "Moment the vehicle was last modified", example = "2026-10-01T11:00:00Z")
        Instant updatedAt) {}
