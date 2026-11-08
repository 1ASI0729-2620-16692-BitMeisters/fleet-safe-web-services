package com.bitmeisters.fleetsafe.platform.iam.infrastructure.email.resend.services;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class EmailServiceImplTest {

    private final StaticMessageSource messageSource = new StaticMessageSource();
    private RestClient.Builder restClientBuilder;
    private MockRestServiceServer resend;

    @BeforeEach
    void setUp() {
        restClientBuilder = RestClient.builder();
        resend = MockRestServiceServer.bindTo(restClientBuilder).build();
        messageSource.addMessage("iam.email.account-opened.subject", Locale.ENGLISH, "Your FleetSafe account is ready");
        messageSource.addMessage("iam.email.account-opened.body", Locale.ENGLISH, "<p>Hello {0}, you are {1}. Sign in with {2}.</p>");
        messageSource.addMessage("iam.email.role.fleet-supervisor", Locale.ENGLISH, "Fleet Supervisor");
        LocaleContextHolder.setLocale(Locale.ENGLISH);
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.resetLocaleContext();
    }

    private EmailServiceImpl emailService(String apiKey, String recipientOverride) {
        return new EmailServiceImpl(restClientBuilder, messageSource, "https://api.resend.com", apiKey,
                "FleetSafe <onboarding@resend.dev>", recipientOverride);
    }

    private void sendToRosa(EmailServiceImpl emailService, String firstName) {
        emailService.sendAccountOpenedEmail(new EmailAddress("rosa@transportesandinos.pe"),
                new PersonName(firstName, "Quispe"), Role.FLEET_SUPERVISOR);
    }

    @Test
    void sendsTheTranslatedAccountEmailThroughResend() {
        resend.expect(requestTo("https://api.resend.com/emails"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer re_test"))
                .andExpect(jsonPath("$.from").value("FleetSafe <onboarding@resend.dev>"))
                .andExpect(jsonPath("$.to[0]").value("rosa@transportesandinos.pe"))
                .andExpect(jsonPath("$.subject").value("Your FleetSafe account is ready"))
                .andExpect(jsonPath("$.html").value(
                        "<p>Hello Rosa, you are Fleet Supervisor. Sign in with rosa@transportesandinos.pe.</p>"))
                .andRespond(withSuccess("{\"id\":\"4ef9a417\"}", MediaType.APPLICATION_JSON));

        sendToRosa(emailService("re_test", ""), "Rosa");

        resend.verify();
    }

    @Test
    void sendsEveryEmailToTheOverrideRecipient() {
        resend.expect(requestTo("https://api.resend.com/emails"))
                .andExpect(jsonPath("$.to[0]").value("owner@fleetsafe.dev"))
                .andRespond(withSuccess());

        sendToRosa(emailService("re_test", "owner@fleetsafe.dev"), "Rosa");

        resend.verify();
    }

    @Test
    void escapesTheNameOfTheRecipient() {
        resend.expect(requestTo("https://api.resend.com/emails"))
                .andExpect(jsonPath("$.html").value(
                        "<p>Hello &lt;b&gt;Rosa&lt;/b&gt;, you are Fleet Supervisor. Sign in with rosa@transportesandinos.pe.</p>"))
                .andRespond(withSuccess());

        sendToRosa(emailService("re_test", ""), "<b>Rosa</b>");

        resend.verify();
    }

    @Test
    void sendsNothingWithoutAnApiKey() {
        sendToRosa(emailService("", ""), "Rosa");

        resend.verify();
    }

    @Test
    void keepsGoingWhenResendRejectsTheEmail() {
        resend.expect(requestTo("https://api.resend.com/emails")).andRespond(withStatus(HttpStatus.FORBIDDEN));

        assertThatCode(() -> sendToRosa(emailService("re_test", ""), "Rosa")).doesNotThrowAnyException();
        resend.verify();
    }
}
