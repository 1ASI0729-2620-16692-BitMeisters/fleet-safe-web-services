package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Vehicle;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public class ResponseEntityFromVehicleQueryResultAssembler {

    public static ResponseEntity<?> toResponseEntityFromList(List<Vehicle> vehicles) {
        var vehicleResources = vehicles.stream()
                .map(VehicleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(vehicleResources);
    }

    public static ResponseEntity<?> toResponseEntityFromOptional(Optional<Vehicle> vehicle, MessageSource messageSource) {
        return vehicle.<ResponseEntity<?>>map(found -> ResponseEntity.ok(VehicleResourceFromEntityAssembler.toResourceFromEntity(found)))
                .orElseGet(() -> {
                    var messageKey = "fleet.vehicle.error.not-found";
                    var message = messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message));
                });
    }
}
