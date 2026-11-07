package com.bitmeisters.fleetsafe.platform.fleet.application.commandservices;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Fleet;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.CreateFleetCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

public interface FleetCommandService {

    Result<Fleet, FleetCommandFailure> handle(CreateFleetCommand command);
}
