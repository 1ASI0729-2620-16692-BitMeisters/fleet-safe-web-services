package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request resource used to add an observation to a result.")
public record AddObservationResource(
        @Schema(description = "What was found", example = "The left brake light does not turn on.", maxLength = 1000, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{inspection.observation.error.description.required}")
        @Size(max = 1000, message = "{inspection.observation.error.description.size}")
        String description) {}
