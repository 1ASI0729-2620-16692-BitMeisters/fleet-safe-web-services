package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationRuleCommandFailure;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.EvaluationRule;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

public class ResponseEntityFromEvaluationRuleCommandResultAssembler {

    private static HttpStatus statusFromFailure(EvaluationRuleCommandFailure failure) {
        return switch (failure) {
            case EvaluationRuleCommandFailure.NotFound notFound -> HttpStatus.NOT_FOUND;
            case EvaluationRuleCommandFailure.Duplicate duplicate -> HttpStatus.CONFLICT;
            case EvaluationRuleCommandFailure.AlreadyInactive alreadyInactive -> HttpStatus.CONFLICT;
        };
    }

    private static String localizedMessage(MessageSource messageSource, String messageKey) {
        return messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
    }

    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<EvaluationRule, EvaluationRuleCommandFailure> result,
            MessageSource messageSource,
            HttpStatus successStatus) {
        return result.fold(
                rule -> new ResponseEntity<>(
                        EvaluationRuleResourceFromEntityAssembler.toResourceFromEntity(rule),
                        successStatus),
                failure -> {
                    var status = statusFromFailure(failure);
                    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                            status,
                            localizedMessage(messageSource, failure.messageKey())));
                });
    }
}
