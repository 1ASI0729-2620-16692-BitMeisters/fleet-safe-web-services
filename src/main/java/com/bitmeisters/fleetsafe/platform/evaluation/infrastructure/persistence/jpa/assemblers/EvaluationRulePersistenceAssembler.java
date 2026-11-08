package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.EvaluationRule;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleName;
import com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.entities.EvaluationRulePersistenceEntity;

public final class EvaluationRulePersistenceAssembler {

    private EvaluationRulePersistenceAssembler() {
    }

    public static EvaluationRule toDomainFromPersistence(EvaluationRulePersistenceEntity entity) {
        if (entity == null) return null;
        return EvaluationRule.restore(
                entity.getId(),
                new InspectionItemId(entity.getInspectionItemId()),
                new RuleName(entity.getName()),
                entity.getFoundResult(),
                entity.getResultingImpact(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static EvaluationRulePersistenceEntity toPersistenceFromDomain(EvaluationRule rule) {
        if (rule == null) return null;
        var entity = new EvaluationRulePersistenceEntity();
        if (rule.getId() != null) {
            entity.setId(rule.getId());
            entity.setCreatedAt(rule.getCreatedAt());
        }
        entity.setInspectionItemId(rule.getInspectionItemId().value());
        entity.setName(rule.getName().value());
        entity.setFoundResult(rule.getFoundResult());
        entity.setResultingImpact(rule.getResultingImpact());
        entity.setDescription(rule.getDescription());
        entity.setActive(rule.isActive());
        return entity;
    }
}
