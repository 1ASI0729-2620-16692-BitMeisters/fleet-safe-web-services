package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

@Schema(description = "Request resource used to register the result of an inspection item.")
public record RegisterInspectionResultResource(
        @Schema(description = "Catalog item evaluated", example = "7c1e2d3f-4a5b-4c6d-8e9f-0a1b2c3d4e5f", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{inspection.result.error.item-id.required}")
        UUID inspectionItemId,
        @Schema(description = "Result of the item", example = "OBSERVED", allowableValues = {"OK", "OBSERVED", "FAIL"}, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{inspection.result.error.value.pattern}")
        @Pattern(regexp = "OK|OBSERVED|FAIL", message = "{inspection.result.error.value.pattern}")
        String result) {}
