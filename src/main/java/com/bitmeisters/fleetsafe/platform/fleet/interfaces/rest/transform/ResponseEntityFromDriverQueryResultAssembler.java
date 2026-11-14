package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Driver;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseEntityFromDriverQueryResultAssembler {

    public static ResponseEntity<?> toResponseEntityFromList(List<Driver> drivers) {
        var driverResources = drivers.stream()
                .map(DriverResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(driverResources);
    }
}
