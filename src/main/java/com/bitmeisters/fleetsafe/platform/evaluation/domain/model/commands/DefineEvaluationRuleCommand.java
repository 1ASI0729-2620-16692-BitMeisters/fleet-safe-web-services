package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleName;

public record DefineEvaluationRuleCommand(
        InspectionItemId inspectionItemId,
        RuleName name,
        ResultValue foundResult,
        RuleImpact resultingImpact,
        String description) {

    public DefineEvaluationRuleCommand {
        if (inspectionItemId == null) throw new IllegalArgumentException("evaluation.rule.error.inspection-item-id.required");
        if (name == null) throw new IllegalArgumentException("evaluation.rule.error.name.required");
        if (foundResult == null) throw new IllegalArgumentException("evaluation.rule.error.found-result.required");
        if (resultingImpact == null) throw new IllegalArgumentException("evaluation.rule.error.resulting-impact.required");
    }
}
