package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseEntityFromOperationalAuthorizationQueryResultAssembler {

    private static ResponseEntity<ProblemDetail> problemDetail(
            HttpStatus status,
            MessageSource messageSource,
            String messageKey, Object... args) {
        var detail = messageSource.getMessage(messageKey, args, messageKey, LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }

    public static ResponseEntity<?> toResponseEntityFromOperationalAuthorization(OperationalAuthorization authorization) {
        return ResponseEntity.ok(OperationalAuthorizationResourceFromEntityAssembler.toResourceFromEntity(authorization));
    }

    public static ResponseEntity<?> toResponseEntityFromList(List<OperationalAuthorization> authorizations) {
        var authorizationResources = authorizations.stream()
                .map(OperationalAuthorizationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(authorizationResources);
    }

    public static ResponseEntity<ProblemDetail> notFound(MessageSource messageSource, String messageKey, Object... args) {
        return problemDetail(HttpStatus.NOT_FOUND, messageSource, messageKey, args);
    }
}
