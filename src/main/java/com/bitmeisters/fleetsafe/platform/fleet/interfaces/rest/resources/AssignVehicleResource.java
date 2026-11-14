package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request resource used to hand a vehicle to a driver.")
public record AssignVehicleResource(
        @Schema(
                description = "Vehicle to assign. It must be an enabled vehicle of your company.",
                example = "4d5e6f7a-8b9c-4d0e-af1b-2c3d4e5f6a7b",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{fleet.vehicle.error.id.required}")
        UUID vehicleId,
        @Schema(
                description = "Driver who receives the vehicle. It must be a driver of your company with the license in force.",
                example = "7b8c9d0e-1f2a-4b3c-8d4e-5f6a7b8c9d0e",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{fleet.driver.error.id.required}")
        UUID driverId) {}
