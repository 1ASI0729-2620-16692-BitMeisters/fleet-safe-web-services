package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices.InspectionItemCommandFailure;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.InspectionItem;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

/**
 * Assembler to transform command Result into standard Spring Web ResponseEntity or RFC 9457 ProblemDetail.
 */
public final class ResponseEntityFromInspectionItemCommandResultAssembler {

    private ResponseEntityFromInspectionItemCommandResultAssembler() {
    }

    private static HttpStatus statusFromFailure(InspectionItemCommandFailure failure) {
        return switch (failure) {
            case InspectionItemCommandFailure.NotFound notFound -> HttpStatus.NOT_FOUND;
            case InspectionItemCommandFailure.DuplicateCode duplicateCode -> HttpStatus.CONFLICT;
            case InspectionItemCommandFailure.AlreadyInactive alreadyInactive -> HttpStatus.CONFLICT;
        };
    }

    private static String localizedMessage(MessageSource messageSource, String messageKey) {
        return messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
    }

    /**
     * Converts a command execution Result into a proper HTTP ResponseEntity.
     *
     * @param result        the Result monad
     * @param messageSource the Spring message source for i18n
     * @param successStatus the HTTP status to return on success
     * @return ResponseEntity with the resource or RFC 9457 ProblemDetail
     */
    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<InspectionItem, InspectionItemCommandFailure> result,
            MessageSource messageSource,
            HttpStatus successStatus) {
        return result.fold(
                item -> new ResponseEntity<>(
                        InspectionItemResourceFromEntityAssembler.toResourceFromEntity(item),
                        successStatus),
                failure -> {
                    var status = statusFromFailure(failure);
                    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                            status,
                            localizedMessage(messageSource, failure.messageKey())));
                });
    }
}
