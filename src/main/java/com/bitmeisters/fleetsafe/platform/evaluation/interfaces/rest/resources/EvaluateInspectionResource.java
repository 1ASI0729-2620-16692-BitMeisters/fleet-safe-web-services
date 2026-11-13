package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Request resource used to evaluate a completed inspection that was not evaluated.")
public record EvaluateInspectionResource(
        @Schema(description = "Completed inspection to evaluate", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{evaluation.error.inspection-id.required}")
        UUID inspectionId) {}
