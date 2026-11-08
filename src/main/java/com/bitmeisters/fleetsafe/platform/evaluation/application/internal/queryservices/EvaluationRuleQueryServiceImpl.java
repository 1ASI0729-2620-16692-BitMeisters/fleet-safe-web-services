package com.bitmeisters.fleetsafe.platform.evaluation.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices.EvaluationRuleQueryService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.EvaluationRule;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetActiveEvaluationRulesQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetEvaluationRuleByIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.EvaluationRuleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EvaluationRuleQueryServiceImpl implements EvaluationRuleQueryService {

    private final EvaluationRuleRepository evaluationRuleRepository;

    public EvaluationRuleQueryServiceImpl(EvaluationRuleRepository evaluationRuleRepository) {
        this.evaluationRuleRepository = evaluationRuleRepository;
    }

    @Override
    public List<EvaluationRule> handle(GetActiveEvaluationRulesQuery query) {
        var results = evaluationRuleRepository.findAllByActiveTrue();
        log.debug("Found {} active evaluation rules", results.size());
        return results;
    }

    @Override
    public Optional<EvaluationRule> handle(GetEvaluationRuleByIdQuery query) {
        var result = evaluationRuleRepository.findById(query.evaluationRuleId());
        if (result.isEmpty()) log.debug("No evaluation rule found for id: {}", query.evaluationRuleId());
        return result;
    }
}
