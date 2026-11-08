package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request resource used to create a fleet in the company of the signed-in user.")
public record CreateFleetResource(
        @Schema(
                description = "Name of the fleet",
                example = "Flota Lima",
                maxLength = 120,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{fleet.error.name.required}")
        @Size(max = 120, message = "{fleet.error.name.size}")
        String name,
        @Schema(
                description = "Optional description of the fleet",
                example = "Flota principal de operaciones de Lima",
                maxLength = 500)
        @Size(max = 500, message = "{fleet.error.description.size}")
        String description) {}
