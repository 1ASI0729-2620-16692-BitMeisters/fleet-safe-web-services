package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.InspectionItem;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Assembler to transform query results into standard Spring Web ResponseEntities or RFC 9457 ProblemDetails.
 */
public final class ResponseEntityFromInspectionItemQueryResultAssembler {

    private ResponseEntityFromInspectionItemQueryResultAssembler() {
    }

    private static ResponseEntity<ProblemDetail> problemDetail(
            HttpStatus status,
            MessageSource messageSource,
            String messageKey,
            Object... args) {
        var detail = messageSource.getMessage(messageKey, args, messageKey, LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }

    /**
     * Converts a single InspectionItem aggregate into an HTTP 200 OK ResponseEntity.
     *
     * @param inspectionItem the inspection item
     * @return 200 OK ResponseEntity containing the resource
     */
    public static ResponseEntity<?> toResponseEntityFromInspectionItem(InspectionItem inspectionItem) {
        return ResponseEntity.ok(InspectionItemResourceFromEntityAssembler.toResourceFromEntity(inspectionItem));
    }

    /**
     * Converts a list of InspectionItem aggregates into an HTTP 200 OK ResponseEntity list.
     *
     * @param inspectionItems the list of inspection items
     * @return 200 OK ResponseEntity containing the list of resources
     */
    public static ResponseEntity<?> toResponseEntityFromList(List<InspectionItem> inspectionItems) {
        var resources = inspectionItems.stream()
                .map(InspectionItemResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(resources);
    }

    /**
     * Produces an HTTP 404 Not Found ProblemDetail response with localized message.
     *
     * @param messageSource the message source
     * @param messageKey    the i18n bundle key
     * @param args          optional message arguments
     * @return 404 Not Found ResponseEntity
     */
    public static ResponseEntity<ProblemDetail> notFound(MessageSource messageSource, String messageKey, Object... args) {
        return problemDetail(HttpStatus.NOT_FOUND, messageSource, messageKey, args);
    }
}
