package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.FleetCommandFailure;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Fleet;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

public class ResponseEntityFromFleetCommandResultAssembler {

    private static HttpStatus statusFromFailure(FleetCommandFailure failure) {
        return switch (failure) {
            case FleetCommandFailure.CompanyNotRegistered companyNotRegistered -> HttpStatus.UNPROCESSABLE_CONTENT;
        };
    }

    private static String localizedMessage(MessageSource messageSource, String messageKey) {
        return messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
    }

    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<Fleet, FleetCommandFailure> result,
            MessageSource messageSource,
            HttpStatus successStatus) {
        return result.fold(
                fleet -> new ResponseEntity<>(
                        FleetResourceFromEntityAssembler.toResourceFromEntity(fleet),
                        successStatus),
                failure -> {
                    var status = statusFromFailure(failure);
                    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                            status,
                            localizedMessage(messageSource, failure.messageKey())));
                });
    }
}
