package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.Evaluation;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.entities.EvaluationDetail;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.entities.EvaluationDetailPersistenceEntity;
import com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.entities.EvaluationPersistenceEntity;

public final class EvaluationPersistenceAssembler {

    private EvaluationPersistenceAssembler() {
    }

    public static Evaluation toDomainFromPersistence(EvaluationPersistenceEntity entity) {
        if (entity == null) return null;
        var details = entity.getDetails().stream()
                .map(detail -> new EvaluationDetail(
                        detail.getId(),
                        new InspectionResultId(detail.getInspectionResultId()),
                        detail.getEvaluationRuleId(),
                        detail.getAppliedImpact()))
                .toList();
        return Evaluation.restore(
                entity.getId(),
                new InspectionId(entity.getInspectionId()),
                entity.getEvaluatedAt(),
                entity.getEvaluatedBy(),
                details,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static EvaluationPersistenceEntity toPersistenceFromDomain(Evaluation evaluation) {
        if (evaluation == null) return null;
        var entity = new EvaluationPersistenceEntity();
        if (evaluation.getId() != null) {
            entity.setId(evaluation.getId());
            entity.setCreatedAt(evaluation.getCreatedAt());
        }
        entity.setInspectionId(evaluation.getInspectionId().value());
        entity.setEvaluatedAt(evaluation.getEvaluatedAt());
        entity.setEvaluatedBy(evaluation.getEvaluatedBy());
        evaluation.getDetails().forEach(detail -> {
            var detailEntity = new EvaluationDetailPersistenceEntity();
            if (detail.getId() != null) detailEntity.setId(detail.getId());
            detailEntity.setEvaluation(entity);
            detailEntity.setInspectionResultId(detail.getInspectionResultId().value());
            detailEntity.setEvaluationRuleId(detail.getEvaluationRuleId());
            detailEntity.setAppliedImpact(detail.getAppliedImpact());
            entity.getDetails().add(detailEntity);
        });
        return entity;
    }
}
