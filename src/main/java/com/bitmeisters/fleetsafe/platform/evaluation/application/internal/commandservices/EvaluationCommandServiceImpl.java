package com.bitmeisters.fleetsafe.platform.evaluation.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationCommandFailure;
import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.Evaluation;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.EvaluateInspectionCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.EvaluationRepository;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.EvaluationRuleRepository;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.OperationalAuthorizationRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class EvaluationCommandServiceImpl implements EvaluationCommandService {

    private final EvaluationRepository evaluationRepository;
    private final EvaluationRuleRepository evaluationRuleRepository;
    private final OperationalAuthorizationRepository operationalAuthorizationRepository;

    public EvaluationCommandServiceImpl(EvaluationRepository evaluationRepository,
                                        EvaluationRuleRepository evaluationRuleRepository,
                                        OperationalAuthorizationRepository operationalAuthorizationRepository) {
        this.evaluationRepository = evaluationRepository;
        this.evaluationRuleRepository = evaluationRuleRepository;
        this.operationalAuthorizationRepository = operationalAuthorizationRepository;
    }

    @Override
    @Transactional
    public Result<OperationalAuthorization, EvaluationCommandFailure> handle(EvaluateInspectionCommand command) {
        if (evaluationRepository.existsByInspectionId(command.inspectionId())) {
            log.warn("Inspection already evaluated: inspectionId={}", command.inspectionId().value());
            return Result.failure(new EvaluationCommandFailure.AlreadyEvaluated());
        }
        if (command.results().isEmpty()) {
            log.warn("Inspection without results: inspectionId={}", command.inspectionId().value());
            return Result.failure(new EvaluationCommandFailure.NothingToEvaluate());
        }
        var activeRules = evaluationRuleRepository.findAllByActiveTrue();
        var evaluation = evaluationRepository.save(new Evaluation(command, activeRules));
        var authorization = operationalAuthorizationRepository.save(
                new OperationalAuthorization(evaluation, command.vehicleId()));
        log.info("Inspection evaluated: inspectionId={}, vehicleId={}, appliedRules={}, status={}",
                command.inspectionId().value(), command.vehicleId().value(),
                evaluation.getDetails().size(), authorization.getStatus());
        return Result.success(authorization);
    }
}
