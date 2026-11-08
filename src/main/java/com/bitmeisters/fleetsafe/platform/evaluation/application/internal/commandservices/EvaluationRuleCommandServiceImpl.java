package com.bitmeisters.fleetsafe.platform.evaluation.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationRuleCommandFailure;
import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationRuleCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.EvaluationRule;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DeactivateEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DefineEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.EvaluationRuleRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class EvaluationRuleCommandServiceImpl implements EvaluationRuleCommandService {

    private final EvaluationRuleRepository evaluationRuleRepository;

    public EvaluationRuleCommandServiceImpl(EvaluationRuleRepository evaluationRuleRepository) {
        this.evaluationRuleRepository = evaluationRuleRepository;
    }

    @Override
    @Transactional
    public Result<EvaluationRule, EvaluationRuleCommandFailure> handle(DefineEvaluationRuleCommand command) {
        if (evaluationRuleRepository.existsByInspectionItemIdAndFoundResultAndActiveTrue(
                command.inspectionItemId(), command.foundResult())) {
            log.warn("Duplicate active evaluation rule: inspectionItemId={}, foundResult={}",
                    command.inspectionItemId().value(), command.foundResult());
            return Result.failure(new EvaluationRuleCommandFailure.Duplicate());
        }
        var evaluationRule = evaluationRuleRepository.save(new EvaluationRule(command));
        log.info("Evaluation rule defined: id={}, inspectionItemId={}, foundResult={}, resultingImpact={}",
                evaluationRule.getId(), command.inspectionItemId().value(), command.foundResult(), command.resultingImpact());
        return Result.success(evaluationRule);
    }

    @Override
    @Transactional
    public Result<EvaluationRule, EvaluationRuleCommandFailure> handle(DeactivateEvaluationRuleCommand command) {
        var evaluationRule = evaluationRuleRepository.findById(command.evaluationRuleId());
        if (evaluationRule.isEmpty()) {
            log.warn("Evaluation rule not found: id={}", command.evaluationRuleId());
            return Result.failure(new EvaluationRuleCommandFailure.NotFound());
        }
        if (!evaluationRule.get().isActive()) {
            log.warn("Evaluation rule already inactive: id={}", command.evaluationRuleId());
            return Result.failure(new EvaluationRuleCommandFailure.AlreadyInactive());
        }
        evaluationRule.get().deactivate();
        var deactivatedEvaluationRule = evaluationRuleRepository.save(evaluationRule.get());
        log.info("Evaluation rule deactivated: id={}", deactivatedEvaluationRule.getId());
        return Result.success(deactivatedEvaluationRule);
    }
}
