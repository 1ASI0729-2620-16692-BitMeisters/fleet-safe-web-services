package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Fleet;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseEntityFromFleetQueryResultAssembler {

    public static ResponseEntity<?> toResponseEntityFromList(List<Fleet> fleets) {
        var fleetResources = fleets.stream()
                .map(FleetResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(fleetResources);
    }
}
