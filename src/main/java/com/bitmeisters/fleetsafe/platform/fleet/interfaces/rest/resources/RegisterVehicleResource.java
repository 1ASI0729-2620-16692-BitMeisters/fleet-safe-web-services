package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request resource used to register a vehicle in a fleet.")
public record RegisterVehicleResource(
        @Schema(
                description = "Fleet the vehicle belongs to. It must be a fleet of your company.",
                example = "3f2b9c6e-1a4d-4e8b-9c2a-7d5e6f8a9b01",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{fleet.error.fleet-id.required}")
        UUID fleetId,
        @Schema(
                description = "License plate, two groups of three letters or digits. It is stored in upper case.",
                example = "ABC-123",
                pattern = "^[A-Za-z0-9]{3}-[A-Za-z0-9]{3}$",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{fleet.vehicle.error.plate.required}")
        @Pattern(regexp = "^[A-Za-z0-9]{3}-[A-Za-z0-9]{3}$", message = "{fleet.vehicle.error.plate.pattern}")
        String plate,
        @Schema(
                description = "Brand of the vehicle",
                example = "Volvo",
                maxLength = 60,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{fleet.vehicle.error.brand.required}")
        @Size(max = 60, message = "{fleet.vehicle.error.brand.size}")
        String brand,
        @Schema(
                description = "Model of the vehicle",
                example = "FH 460",
                maxLength = 60,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{fleet.vehicle.error.model.required}")
        @Size(max = 60, message = "{fleet.vehicle.error.model.size}")
        String model,
        @Schema(
                description = "Manufacturing year, from 1950 up to next year",
                example = "2024",
                minimum = "1950",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{fleet.vehicle.error.year.required}")
        @Min(value = 1950, message = "{fleet.vehicle.error.year.range}")
        Integer year,
        @Schema(
                description = "Size category of the vehicle",
                example = "LARGE",
                allowableValues = {"THIN", "MEDIUM", "LARGE"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{fleet.vehicle.error.type.required}")
        @Pattern(regexp = "THIN|MEDIUM|LARGE", message = "{fleet.vehicle.error.type.pattern}")
        String type,
        @Schema(
                description = "Load capacity of the vehicle, greater than zero",
                example = "20",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{fleet.vehicle.error.capacity.required}")
        @DecimalMin(value = "0", inclusive = false, message = "{fleet.vehicle.error.capacity.positive}")
        @DecimalMax(value = "99999999.99", message = "{fleet.vehicle.error.capacity.max}")
        BigDecimal capacity) {}
