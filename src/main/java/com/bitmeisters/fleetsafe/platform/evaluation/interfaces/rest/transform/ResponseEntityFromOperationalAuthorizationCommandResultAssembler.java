package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.OperationalAuthorizationCommandFailure;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

public class ResponseEntityFromOperationalAuthorizationCommandResultAssembler {

    private static HttpStatus statusFromFailure(OperationalAuthorizationCommandFailure failure) {
        return switch (failure) {
            case OperationalAuthorizationCommandFailure.NotFound notFound -> HttpStatus.NOT_FOUND;
            case OperationalAuthorizationCommandFailure.NotBlocked notBlocked -> HttpStatus.CONFLICT;
        };
    }

    private static String localizedMessage(MessageSource messageSource, String messageKey) {
        return messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
    }

    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<OperationalAuthorization, OperationalAuthorizationCommandFailure> result,
            MessageSource messageSource,
            HttpStatus successStatus) {
        return result.fold(
                authorization -> new ResponseEntity<>(
                        OperationalAuthorizationResourceFromEntityAssembler.toResourceFromEntity(authorization),
                        successStatus),
                failure -> {
                    var status = statusFromFailure(failure);
                    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                            status,
                            localizedMessage(messageSource, failure.messageKey())));
                });
    }
}
