package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationCommandFailure;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

public class ResponseEntityFromEvaluationCommandResultAssembler {

    private static HttpStatus statusFromFailure(EvaluationCommandFailure failure) {
        return switch (failure) {
            case EvaluationCommandFailure.AlreadyEvaluated alreadyEvaluated -> HttpStatus.CONFLICT;
            case EvaluationCommandFailure.NothingToEvaluate nothingToEvaluate -> HttpStatus.UNPROCESSABLE_CONTENT;
        };
    }

    private static String localizedMessage(MessageSource messageSource, String messageKey) {
        return messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
    }

    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<OperationalAuthorization, EvaluationCommandFailure> result,
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
