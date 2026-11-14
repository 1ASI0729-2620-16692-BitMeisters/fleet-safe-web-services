package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Summary of a vehicle, as shown inside the assignment of a driver.")
public record VehicleSummaryResource(
        @Schema(description = "Persistent identifier", example = "4d5e6f7a-8b9c-4d0e-af1b-2c3d4e5f6a7b")
        UUID id,
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
        @Schema(description = "Current operational condition", example = "ENABLED",
                allowableValues = {"ENABLED", "OBSERVED", "NOT_ENABLED"})
        String currentStatus) {}
