package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Company;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public class ResponseEntityFromCompanyQueryResultAssembler {

    public static ResponseEntity<?> toResponseEntityFromOptional(Optional<Company> company, MessageSource messageSource) {
        return company.<ResponseEntity<?>>map(found -> ResponseEntity.ok(CompanyResourceFromEntityAssembler.toResourceFromEntity(found)))
                .orElseGet(() -> {
                    var messageKey = "fleet.company.error.not-found";
                    var message = messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, message));
                });
    }
}
