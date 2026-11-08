package com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.EvaluationRule;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluationRuleRepository {

    Optional<EvaluationRule> findById(UUID id);

    List<EvaluationRule> findAllByActiveTrue();

    boolean existsByInspectionItemIdAndFoundResultAndActiveTrue(InspectionItemId inspectionItemId, ResultValue foundResult);

    EvaluationRule save(EvaluationRule evaluationRule);
}
