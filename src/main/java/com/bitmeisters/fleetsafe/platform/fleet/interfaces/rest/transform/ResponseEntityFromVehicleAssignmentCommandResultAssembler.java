package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.VehicleAssignmentCommandFailure;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.VehicleAssignment;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

public class ResponseEntityFromVehicleAssignmentCommandResultAssembler {

    private static HttpStatus statusFromFailure(VehicleAssignmentCommandFailure failure) {
        return switch (failure) {
            case VehicleAssignmentCommandFailure.VehicleNotFound vehicleNotFound -> HttpStatus.UNPROCESSABLE_CONTENT;
            case VehicleAssignmentCommandFailure.DriverNotFound driverNotFound -> HttpStatus.UNPROCESSABLE_CONTENT;
            case VehicleAssignmentCommandFailure.VehicleNotEnabled vehicleNotEnabled -> HttpStatus.UNPROCESSABLE_CONTENT;
            case VehicleAssignmentCommandFailure.DriverLicenseExpired driverLicenseExpired -> HttpStatus.UNPROCESSABLE_CONTENT;
            case VehicleAssignmentCommandFailure.VehicleAlreadyAssigned vehicleAlreadyAssigned -> HttpStatus.CONFLICT;
            case VehicleAssignmentCommandFailure.DriverAlreadyAssigned driverAlreadyAssigned -> HttpStatus.CONFLICT;
            case VehicleAssignmentCommandFailure.AlreadyClosed alreadyClosed -> HttpStatus.CONFLICT;
            case VehicleAssignmentCommandFailure.NotFound notFound -> HttpStatus.NOT_FOUND;
        };
    }

    private static String localizedMessage(MessageSource messageSource, String messageKey) {
        return messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
    }

    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<VehicleAssignment, VehicleAssignmentCommandFailure> result,
            MessageSource messageSource,
            HttpStatus successStatus) {
        return result.fold(
                assignment -> new ResponseEntity<>(
                        VehicleAssignmentResourceFromEntityAssembler.toResourceFromEntity(assignment),
                        successStatus),
                failure -> {
                    var status = statusFromFailure(failure);
                    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                            status,
                            localizedMessage(messageSource, failure.messageKey())));
                });
    }
}
