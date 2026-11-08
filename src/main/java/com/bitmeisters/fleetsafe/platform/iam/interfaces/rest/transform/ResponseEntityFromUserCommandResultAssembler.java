package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandFailure;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

public class ResponseEntityFromUserCommandResultAssembler {

    private static HttpStatus statusFromFailure(UserCommandFailure failure) {
        return switch (failure) {
            case UserCommandFailure.EmailAlreadyRegistered emailAlreadyRegistered -> HttpStatus.CONFLICT;
            case UserCommandFailure.NotFound notFound -> HttpStatus.NOT_FOUND;
            case UserCommandFailure.AlreadyInactive alreadyInactive -> HttpStatus.CONFLICT;
            case UserCommandFailure.InvalidCredentials invalidCredentials -> HttpStatus.UNAUTHORIZED;
            case UserCommandFailure.Inactive inactive -> HttpStatus.UNAUTHORIZED;
        };
    }

    private static String localizedMessage(MessageSource messageSource, String messageKey) {
        return messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
    }

    public static <T, R> ResponseEntity<?> toResponseEntityFromResult(
            Result<T, UserCommandFailure> result,
            Function<T, R> successResourceAssembler,
            MessageSource messageSource,
            HttpStatus successStatus) {
        return result.fold(
                value -> new ResponseEntity<>(successResourceAssembler.apply(value), successStatus),
                failure -> {
                    var status = statusFromFailure(failure);
                    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(
                            status,
                            localizedMessage(messageSource, failure.messageKey())));
                });
    }
}
