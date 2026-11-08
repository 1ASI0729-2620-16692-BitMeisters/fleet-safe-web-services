package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.EvaluationRule;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources.EvaluationRuleResource;

public class EvaluationRuleResourceFromEntityAssembler {

    public static EvaluationRuleResource toResourceFromEntity(EvaluationRule entity) {
        return new EvaluationRuleResource(
                entity.getId(),
                entity.getInspectionItemId().value(),
                entity.getName().value(),
                entity.getFoundResult().name(),
                entity.getResultingImpact().name(),
                entity.getDescription(),
                entity.isActive());
    }
}
