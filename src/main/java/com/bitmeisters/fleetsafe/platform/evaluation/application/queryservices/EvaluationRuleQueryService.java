package com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.EvaluationRule;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetActiveEvaluationRulesQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetEvaluationRuleByIdQuery;

import java.util.List;
import java.util.Optional;

public interface EvaluationRuleQueryService {

    List<EvaluationRule> handle(GetActiveEvaluationRulesQuery query);

    Optional<EvaluationRule> handle(GetEvaluationRuleByIdQuery query);
}
