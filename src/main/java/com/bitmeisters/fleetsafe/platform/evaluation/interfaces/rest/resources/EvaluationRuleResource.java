package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response resource representing an evaluation rule.")
public record EvaluationRuleResource(
        @Schema(description = "Persistent identifier", example = "3f2b9c6e-1a4d-4e8b-9c2a-7d5e6f8a9b01")
        UUID id,
        @Schema(description = "Inspection item the rule applies to", example = "496e45e1-43d4-59dc-ae40-0b5e4300743b")
        UUID inspectionItemId,
        @Schema(description = "Readable name of the rule", example = "Brake system failure blocks the vehicle")
        String name,
        @Schema(description = "Inspection result that triggers the rule", example = "FAIL")
        String foundResult,
        @Schema(description = "Impact the rule has on the vehicle", example = "BLOCKING")
        String resultingImpact,
        @Schema(description = "Optional explanation of the rule", example = "A failed brake system makes the vehicle unsafe to operate.")
        String description,
        @Schema(description = "Whether the rule is applied in new evaluations", example = "true")
        boolean isActive) {}
