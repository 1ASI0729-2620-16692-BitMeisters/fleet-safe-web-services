package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request resource used to start a pre-operational inspection.")
public record StartInspectionResource(
        @Schema(description = "Vehicle to inspect", example = "9a8b7c6d-5e4f-4a3b-8c2d-1e0f9a8b7c6d", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{inspection.error.vehicle-id.required}")
        UUID vehicleId,
        @Schema(description = "Odometer reading in kilometers", example = "125430", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{inspection.error.odometer.invalid}")
        @Min(value = 0, message = "{inspection.error.odometer.invalid}")
        Integer odometer) {}
