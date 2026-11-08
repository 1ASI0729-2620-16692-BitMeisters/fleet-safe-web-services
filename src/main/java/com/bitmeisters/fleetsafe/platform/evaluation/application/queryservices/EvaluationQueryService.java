package com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.Evaluation;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetEvaluationByInspectionIdQuery;

import java.util.Optional;

public interface EvaluationQueryService {

    Optional<Evaluation> handle(GetEvaluationByInspectionIdQuery query);
}
