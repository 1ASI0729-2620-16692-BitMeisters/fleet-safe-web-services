package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices.InspectionCommandFailure;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.Inspection;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

public final class ResponseEntityFromInspectionCommandResultAssembler {

    private ResponseEntityFromInspectionCommandResultAssembler() {
    }

    private static HttpStatus statusFromFailure(InspectionCommandFailure failure) {
        return switch (failure) {
            case InspectionCommandFailure.NotFound notFound -> HttpStatus.NOT_FOUND;
            case InspectionCommandFailure.ResultNotFound resultNotFound -> HttpStatus.NOT_FOUND;
            case InspectionCommandFailure.ObservationNotFound observationNotFound -> HttpStatus.NOT_FOUND;
            case InspectionCommandFailure.ItemNotFound itemNotFound -> HttpStatus.NOT_FOUND;
            case InspectionCommandFailure.NotInProgress notInProgress -> HttpStatus.CONFLICT;
            case InspectionCommandFailure.ItemAlreadyRegistered itemAlreadyRegistered -> HttpStatus.CONFLICT;
            case InspectionCommandFailure.VehicleHasOngoingInspection ongoingInspection -> HttpStatus.CONFLICT;
            case InspectionCommandFailure.MissingItems missingItems -> HttpStatus.CONFLICT;
            case InspectionCommandFailure.MissingObservation missingObservation -> HttpStatus.CONFLICT;
            case InspectionCommandFailure.MissingEvidence missingEvidence -> HttpStatus.CONFLICT;
        };
    }

    public static ResponseEntity<?> toProblem(HttpStatus status, String messageKey, MessageSource messageSource) {
        var detail = messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }

    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<Inspection, InspectionCommandFailure> result,
            Function<Inspection, Object> resourceAssembler,
            MessageSource messageSource,
            HttpStatus successStatus) {
        return result.fold(
                inspection -> new ResponseEntity<>(resourceAssembler.apply(inspection), successStatus),
                failure -> toProblem(statusFromFailure(failure), failure.messageKey(), messageSource));
    }
}
