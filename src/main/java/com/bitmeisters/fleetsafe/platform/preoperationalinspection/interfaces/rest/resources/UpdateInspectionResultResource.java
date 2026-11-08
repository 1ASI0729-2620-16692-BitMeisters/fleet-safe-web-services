package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request resource used to change the result of an inspection item.")
public record UpdateInspectionResultResource(
        @Schema(description = "New result of the item", example = "OK", allowableValues = {"OK", "OBSERVED", "FAIL"}, requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{inspection.result.error.value.pattern}")
        @Pattern(regexp = "OK|OBSERVED|FAIL", message = "{inspection.result.error.value.pattern}")
        String result) {}
