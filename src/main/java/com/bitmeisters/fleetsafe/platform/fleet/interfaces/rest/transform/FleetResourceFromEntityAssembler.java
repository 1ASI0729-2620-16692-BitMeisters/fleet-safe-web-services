package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Fleet;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.FleetResource;

public class FleetResourceFromEntityAssembler {

    public static FleetResource toResourceFromEntity(Fleet entity) {
        return new FleetResource(
                entity.getId(),
                entity.getCompanyId().value(),
                entity.getName().value(),
                entity.getDescription(),
                entity.getCreatedAt());
    }
}
