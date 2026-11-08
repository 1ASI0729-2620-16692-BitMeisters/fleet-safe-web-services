package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DefineEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources.DefineEvaluationRuleResource;

public class DefineEvaluationRuleCommandFromResourceAssembler {

    public static DefineEvaluationRuleCommand toCommandFromResource(DefineEvaluationRuleResource resource) {
        return new DefineEvaluationRuleCommand(
                EvaluationValueObjectFromStringAssembler.toInspectionItemIdFromUuid(resource.inspectionItemId()),
                EvaluationValueObjectFromStringAssembler.toRuleNameFromString(resource.name()),
                EvaluationValueObjectFromStringAssembler.toResultValueFromString(resource.foundResult()),
                EvaluationValueObjectFromStringAssembler.toRuleImpactFromString(resource.resultingImpact()),
                resource.description());
    }
}
