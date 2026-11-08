package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.entities;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import lombok.Getter;

import java.util.UUID;

@Getter
public class EvaluationDetail {

    private final UUID id;
    private final InspectionResultId inspectionResultId;
    private final UUID evaluationRuleId;
    private final RuleImpact appliedImpact;

    public EvaluationDetail(InspectionResultId inspectionResultId, UUID evaluationRuleId, RuleImpact appliedImpact) {
        this(null, inspectionResultId, evaluationRuleId, appliedImpact);
    }

    public EvaluationDetail(UUID id, InspectionResultId inspectionResultId, UUID evaluationRuleId, RuleImpact appliedImpact) {
        this.id = id;
        this.inspectionResultId = inspectionResultId;
        this.evaluationRuleId = evaluationRuleId;
        this.appliedImpact = appliedImpact;
    }
}
