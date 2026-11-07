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

@Schema(description = "Request resource used to change the information of a vehicle. The plate, the fleet and the status cannot be changed here.")
public record UpdateVehicleResource(
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
