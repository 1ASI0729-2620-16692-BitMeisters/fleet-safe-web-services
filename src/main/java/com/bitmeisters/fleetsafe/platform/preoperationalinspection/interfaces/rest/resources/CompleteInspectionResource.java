package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request resource used to complete an inspection.")
public record CompleteInspectionResource(
        @Schema(description = "Only COMPLETED is accepted", example = "COMPLETED", allowableValues = {"COMPLETED"}, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{inspection.error.status.pattern}")
        @Pattern(regexp = "COMPLETED", message = "{inspection.error.status.pattern}")
        String status) {}
