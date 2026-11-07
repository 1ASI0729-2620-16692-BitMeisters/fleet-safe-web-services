package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.UpdateVehicleCommand;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.UpdateVehicleResource;

import java.util.UUID;

public class UpdateVehicleCommandFromResourceAssembler {

    public static UpdateVehicleCommand toCommandFromResource(UUID companyId, UUID vehicleId, UpdateVehicleResource resource) {
        return new UpdateVehicleCommand(
                FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(companyId),
                FleetValueObjectFromStringAssembler.toVehicleIdFromUuid(vehicleId),
                FleetValueObjectFromStringAssembler.toVehicleBrandFromString(resource.brand()),
                FleetValueObjectFromStringAssembler.toVehicleModelFromString(resource.model()),
                FleetValueObjectFromStringAssembler.toManufacturingYearFromInteger(resource.year()),
                FleetValueObjectFromStringAssembler.toVehicleTypeFromString(resource.type()),
                FleetValueObjectFromStringAssembler.toLoadCapacityFromDecimal(resource.capacity()));
    }
}
