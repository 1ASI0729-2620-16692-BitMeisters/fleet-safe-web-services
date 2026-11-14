package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Driver;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.DriverResource;

public class DriverResourceFromEntityAssembler {

    public static DriverResource toResourceFromEntity(Driver entity) {
        return new DriverResource(
                entity.getId(),
                entity.getUserId().value(),
                entity.getLicenseNumber().value(),
                entity.getLicenseExpirationDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
