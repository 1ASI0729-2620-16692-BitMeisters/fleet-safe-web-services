package com.bitmeisters.fleetsafe.platform.fleet.application.commandservices;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Driver;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.RegisterDriverCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

public interface DriverCommandService {

    Result<Driver, DriverCommandFailure> handle(RegisterDriverCommand command);
}
