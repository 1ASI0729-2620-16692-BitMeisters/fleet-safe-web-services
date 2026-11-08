package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.documentation.application.commandservices.VehicleDocumentCommandFailure;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

public class ResponseEntityFromVehicleDocumentCommandResultAssembler {

    private static HttpStatus statusFromFailure(VehicleDocumentCommandFailure failure) {
        return switch (failure) {
            case VehicleDocumentCommandFailure.NotFound notFound -> HttpStatus.NOT_FOUND;
            case VehicleDocumentCommandFailure.UnknownDocumentType unknownDocumentType -> HttpStatus.UNPROCESSABLE_CONTENT;
        };
    }

    private static String localizedMessage(MessageSource messageSource, String messageKey) {
        return messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
    }

    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<VehicleDocument, VehicleDocumentCommandFailure> result,
            MessageSource messageSource,
            HttpStatus successStatus) {
        return result.fold(
                vehicleDocument -> new ResponseEntity<>(
                        VehicleDocumentResourceFromEntityAssembler.toResourceFromEntity(vehicleDocument),
                        successStatus),
                failure -> {
                    var status = statusFromFailure(failure);
                    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                            status,
                            localizedMessage(messageSource, failure.messageKey())));
                });
    }
}
