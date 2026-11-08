package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.CreateFleetCommand;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.CreateFleetResource;

import java.util.UUID;

public class CreateFleetCommandFromResourceAssembler {

    public static CreateFleetCommand toCommandFromResource(UUID companyId, CreateFleetResource resource) {
        return new CreateFleetCommand(
                FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(companyId),
                FleetValueObjectFromStringAssembler.toFleetNameFromString(resource.name()),
                resource.description());
    }
}
