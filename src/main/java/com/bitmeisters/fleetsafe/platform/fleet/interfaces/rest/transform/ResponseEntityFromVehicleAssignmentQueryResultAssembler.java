package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.application.queryservices.ActiveVehicleAssignment;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.VehicleAssignment;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public class ResponseEntityFromVehicleAssignmentQueryResultAssembler {

    public static ResponseEntity<?> toResponseEntityFromList(List<VehicleAssignment> assignments) {
        var assignmentResources = assignments.stream()
                .map(VehicleAssignmentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(assignmentResources);
    }

    public static ResponseEntity<?> toResponseEntityFromOptional(Optional<ActiveVehicleAssignment> assignment,
                                                                 MessageSource messageSource) {
        return assignment.<ResponseEntity<?>>map(found -> ResponseEntity.ok(
                        VehicleAssignmentResourceFromEntityAssembler.toDriverResourceFromEntity(found)))
                .orElseGet(() -> {
                    var messageKey = "fleet.assignment.error.none-active";
                    var message = messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message));
                });
    }
}
