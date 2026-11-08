package com.bitmeisters.fleetsafe.platform.fleet.application.commandservices;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Vehicle;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.RegisterVehicleCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.UpdateVehicleCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

public interface VehicleCommandService {

    Result<Vehicle, VehicleCommandFailure> handle(RegisterVehicleCommand command);

    Result<Vehicle, VehicleCommandFailure> handle(UpdateVehicleCommand command);
}
