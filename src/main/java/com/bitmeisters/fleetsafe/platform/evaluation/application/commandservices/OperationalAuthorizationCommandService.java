package com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.LiftBlockCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

public interface OperationalAuthorizationCommandService {

    Result<OperationalAuthorization, OperationalAuthorizationCommandFailure> handle(LiftBlockCommand command);
}
