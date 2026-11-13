package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request resource used to change an evaluation rule. A rule can only be deactivated.")
public record UpdateEvaluationRuleResource(
        @Schema(description = "false deactivates the rule", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{evaluation.rule.error.active.required}")
        @AssertFalse(message = "{evaluation.rule.error.only-deactivation}")
        Boolean isActive) {}
