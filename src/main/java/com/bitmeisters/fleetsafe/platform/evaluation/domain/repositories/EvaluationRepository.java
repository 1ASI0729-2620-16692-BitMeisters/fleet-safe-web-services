package com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.Evaluation;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionId;

import java.util.Optional;

public interface EvaluationRepository {

    boolean existsByInspectionId(InspectionId inspectionId);

    Optional<Evaluation> findByInspectionId(InspectionId inspectionId);

    Evaluation save(Evaluation evaluation);
}
