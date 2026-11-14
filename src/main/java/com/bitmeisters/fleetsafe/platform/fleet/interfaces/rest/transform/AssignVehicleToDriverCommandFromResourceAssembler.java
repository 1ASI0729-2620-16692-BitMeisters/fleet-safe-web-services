package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.AssignVehicleToDriverCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.AssignVehicleResource;

import java.util.UUID;

public class AssignVehicleToDriverCommandFromResourceAssembler {

    public static AssignVehicleToDriverCommand toCommandFromResource(UUID companyId, AssignVehicleResource resource) {
        return new AssignVehicleToDriverCommand(
                FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(companyId),
                FleetValueObjectFromStringAssembler.toVehicleIdFromUuid(resource.vehicleId()),
                new DriverId(resource.driverId()));
    }
}
