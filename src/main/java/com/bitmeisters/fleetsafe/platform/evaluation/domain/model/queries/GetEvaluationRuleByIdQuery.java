package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries;

import java.util.UUID;

public record GetEvaluationRuleByIdQuery(UUID evaluationRuleId) {

    public GetEvaluationRuleByIdQuery {
        if (evaluationRuleId == null) throw new IllegalArgumentException("evaluation.rule.error.id.required");
    }
}
