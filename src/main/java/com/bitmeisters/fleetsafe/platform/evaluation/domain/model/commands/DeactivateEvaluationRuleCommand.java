package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands;

import java.util.UUID;

public record DeactivateEvaluationRuleCommand(UUID evaluationRuleId) {

    public DeactivateEvaluationRuleCommand {
        if (evaluationRuleId == null) throw new IllegalArgumentException("evaluation.rule.error.id.required");
    }
}
