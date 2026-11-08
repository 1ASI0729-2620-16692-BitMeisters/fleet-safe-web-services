package com.bitmeisters.fleetsafe.platform.evaluation.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices.EvaluationQueryService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.Evaluation;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetEvaluationByInspectionIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.EvaluationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class EvaluationQueryServiceImpl implements EvaluationQueryService {

    private final EvaluationRepository evaluationRepository;

    public EvaluationQueryServiceImpl(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    @Override
    public Optional<Evaluation> handle(GetEvaluationByInspectionIdQuery query) {
        var result = evaluationRepository.findByInspectionId(query.inspectionId());
        if (result.isEmpty()) log.debug("No evaluation found for inspectionId: {}", query.inspectionId().value());
        return result;
    }
}
