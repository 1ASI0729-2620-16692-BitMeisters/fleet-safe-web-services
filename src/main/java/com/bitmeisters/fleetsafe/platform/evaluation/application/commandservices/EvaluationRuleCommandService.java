package com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.EvaluationRule;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DeactivateEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DefineEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

public interface EvaluationRuleCommandService {

    Result<EvaluationRule, EvaluationRuleCommandFailure> handle(DefineEvaluationRuleCommand command);

    Result<EvaluationRule, EvaluationRuleCommandFailure> handle(DeactivateEvaluationRuleCommand command);
}
