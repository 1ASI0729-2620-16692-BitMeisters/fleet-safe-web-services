package com.bitmeisters.fleetsafe.platform.fleet.application.commandservices;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.VehicleAssignment;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.AssignVehicleToDriverCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.CloseVehicleAssignmentCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

public interface VehicleAssignmentCommandService {

    Result<VehicleAssignment, VehicleAssignmentCommandFailure> handle(AssignVehicleToDriverCommand command);

    Result<VehicleAssignment, VehicleAssignmentCommandFailure> handle(CloseVehicleAssignmentCommand command);
}
