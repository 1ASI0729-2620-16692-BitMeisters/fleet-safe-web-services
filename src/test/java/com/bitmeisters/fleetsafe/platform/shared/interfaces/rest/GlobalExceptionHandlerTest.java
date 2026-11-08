package com.bitmeisters.fleetsafe.platform.shared.interfaces.rest;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final StaticMessageSource messageSource = new StaticMessageSource();
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler(messageSource);

    @Test
    void translatesAKnownMessageKey() {
        messageSource.addMessage("evaluation.rule.error.name.required", Locale.ENGLISH, "The rule name is required.");

        var response = handler.handleException(new IllegalArgumentException("evaluation.rule.error.name.required"), Locale.ENGLISH);

        assertThat(response.getBody().getDetail()).isEqualTo("The rule name is required.");
    }

    @Test
    void answersWithTheKeyWhenItHasNoTranslation() {
        var response = handler.handleException(new IllegalArgumentException("some.missing.key"), Locale.ENGLISH);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody().getDetail()).isEqualTo("some.missing.key");
    }
}
