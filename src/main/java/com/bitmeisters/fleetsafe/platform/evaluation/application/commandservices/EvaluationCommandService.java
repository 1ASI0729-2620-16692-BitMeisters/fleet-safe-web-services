package com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.EvaluateInspectionCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

public interface EvaluationCommandService {

    Result<OperationalAuthorization, EvaluationCommandFailure> handle(EvaluateInspectionCommand command);
}
