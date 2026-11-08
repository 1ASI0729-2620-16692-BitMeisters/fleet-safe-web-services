package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.LiftBlockCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources.LiftBlockResource;

import java.util.UUID;

public class LiftBlockCommandFromResourceAssembler {

    public static LiftBlockCommand toCommandFromResource(UUID vehicleId, LiftBlockResource resource, UUID authorizedBy) {
        return new LiftBlockCommand(new VehicleId(vehicleId), resource.reason(), authorizedBy);
    }
}
