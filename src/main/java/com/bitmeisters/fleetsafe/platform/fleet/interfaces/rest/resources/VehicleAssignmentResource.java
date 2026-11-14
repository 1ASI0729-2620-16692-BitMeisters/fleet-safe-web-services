package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Response resource representing the assignment of a vehicle to a driver.")
public record VehicleAssignmentResource(
        @Schema(description = "Persistent identifier", example = "9a8b7c6d-5e4f-4a3b-8c2d-1e0f9a8b7c6d")
        UUID id,
        @Schema(description = "Vehicle handed to the driver", example = "4d5e6f7a-8b9c-4d0e-af1b-2c3d4e5f6a7b")
        UUID vehicleId,
        @Schema(description = "Driver who received the vehicle", example = "7b8c9d0e-1f2a-4b3c-8d4e-5f6a7b8c9d0e")
        UUID driverId,
        @Schema(description = "Day the assignment started", example = "2026-11-14")
        LocalDate assignedFrom,
        @Schema(description = "Day the assignment ended. Empty while it is active.", example = "null", nullable = true)
        LocalDate assignedTo,
        @Schema(description = "Whether the assignment is still in force", example = "true")
        boolean isActive,
        @Schema(description = "Moment the assignment was registered", example = "2026-11-14T14:00:00Z")
        Instant createdAt,
        @Schema(description = "Moment the assignment was last modified", example = "2026-11-14T14:00:00Z")
        Instant updatedAt) {}
