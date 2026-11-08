package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.RegisterVehicleCommand;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.RegisterVehicleResource;

import java.util.UUID;

public class RegisterVehicleCommandFromResourceAssembler {

    public static RegisterVehicleCommand toCommandFromResource(UUID companyId, RegisterVehicleResource resource) {
        return new RegisterVehicleCommand(
                FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(companyId),
                FleetValueObjectFromStringAssembler.toFleetIdFromUuid(resource.fleetId()),
                FleetValueObjectFromStringAssembler.toPlateFromString(resource.plate()),
                FleetValueObjectFromStringAssembler.toVehicleBrandFromString(resource.brand()),
                FleetValueObjectFromStringAssembler.toVehicleModelFromString(resource.model()),
                FleetValueObjectFromStringAssembler.toManufacturingYearFromInteger(resource.year()),
                FleetValueObjectFromStringAssembler.toVehicleTypeFromString(resource.type()),
                FleetValueObjectFromStringAssembler.toLoadCapacityFromDecimal(resource.capacity()));
    }
}
