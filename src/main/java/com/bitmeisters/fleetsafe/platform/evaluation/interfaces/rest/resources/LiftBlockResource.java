package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request resource used to lift the block of a vehicle that is not enabled.")
public record LiftBlockResource(
        @Schema(
                description = "Why the vehicle is authorized to operate despite the block",
                example = "Urgent delivery approved by operations; brakes will be repaired on return.",
                maxLength = 500,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{evaluation.authorization.error.reason.required}")
        @Size(max = 500, message = "{evaluation.authorization.error.reason.size}")
        String reason) {}
