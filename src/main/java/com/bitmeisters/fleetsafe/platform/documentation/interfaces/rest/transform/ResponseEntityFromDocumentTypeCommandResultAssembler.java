package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.documentation.application.commandservices.DocumentTypeCommandFailure;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.DocumentType;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

public class ResponseEntityFromDocumentTypeCommandResultAssembler {

    private static HttpStatus statusFromFailure(DocumentTypeCommandFailure failure) {
        return switch (failure) {
            case DocumentTypeCommandFailure.DuplicateCode duplicateCode -> HttpStatus.CONFLICT;
        };
    }

    private static String localizedMessage(MessageSource messageSource, String messageKey) {
        return messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
    }

    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<DocumentType, DocumentTypeCommandFailure> result,
            MessageSource messageSource,
            HttpStatus successStatus) {
        return result.fold(
                documentType -> new ResponseEntity<>(
                        DocumentTypeResourceFromEntityAssembler.toResourceFromEntity(documentType),
                        successStatus),
                failure -> {
                    var status = statusFromFailure(failure);
                    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                            status,
                            localizedMessage(messageSource, failure.messageKey())));
                });
    }
}
