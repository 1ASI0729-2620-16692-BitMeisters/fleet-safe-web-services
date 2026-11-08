package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Request resource used to define an evaluation rule.")
public record DefineEvaluationRuleResource(
        @Schema(
                description = "Inspection item the rule applies to",
                example = "496e45e1-43d4-59dc-ae40-0b5e4300743b",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{evaluation.rule.error.inspection-item-id.required}")
        UUID inspectionItemId,
        @Schema(
                description = "Readable name of the rule",
                example = "Brake system failure blocks the vehicle",
                maxLength = 120,
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{evaluation.rule.error.name.required}")
        @Size(max = 120, message = "{evaluation.rule.error.name.size}")
        String name,
        @Schema(
                description = "Inspection result that triggers the rule",
                example = "FAIL",
                allowableValues = {"OK", "OBSERVED", "FAIL"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{evaluation.rule.error.found-result.required}")
        @Pattern(regexp = "OK|OBSERVED|FAIL", message = "{evaluation.rule.error.found-result.pattern}")
        String foundResult,
        @Schema(
                description = "Impact the rule has on the vehicle",
                example = "BLOCKING",
                allowableValues = {"NONE", "OBSERVED", "BLOCKING"},
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{evaluation.rule.error.resulting-impact.required}")
        @Pattern(regexp = "NONE|OBSERVED|BLOCKING", message = "{evaluation.rule.error.resulting-impact.pattern}")
        String resultingImpact,
        @Schema(
                description = "Optional explanation of the rule",
                example = "A failed brake system makes the vehicle unsafe to operate.",
                maxLength = 500)
        @Size(max = 500, message = "{evaluation.rule.error.description.size}")
        String description) {}
