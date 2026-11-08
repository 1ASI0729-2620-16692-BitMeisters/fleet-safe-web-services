package com.bitmeisters.fleetsafe.platform.iam.infrastructure.email.resend.services;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import com.bitmeisters.fleetsafe.platform.iam.infrastructure.email.resend.ResendEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.HtmlUtils;

import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class EmailServiceImpl implements ResendEmailService {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    private final RestClient restClient;
    private final MessageSource messageSource;
    private final String apiKey;
    private final String sender;
    private final String recipientOverride;

    @Autowired
    public EmailServiceImpl(MessageSource messageSource,
                            @Value("${resend.base-url}") String baseUrl,
                            @Value("${resend.api-key:}") String apiKey,
                            @Value("${resend.from}") String sender,
                            @Value("${resend.recipient-override:}") String recipientOverride) {
        this(RestClient.builder().requestFactory(requestFactory()), messageSource, baseUrl, apiKey, sender, recipientOverride);
    }

    EmailServiceImpl(RestClient.Builder restClientBuilder, MessageSource messageSource, String baseUrl,
                     String apiKey, String sender, String recipientOverride) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        this.messageSource = messageSource;
        this.apiKey = apiKey;
        this.sender = sender;
        this.recipientOverride = recipientOverride;
    }

    private static SimpleClientHttpRequestFactory requestFactory() {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIMEOUT);
        requestFactory.setReadTimeout(TIMEOUT);
        return requestFactory;
    }

    @Override
    public void sendAccountOpenedEmail(EmailAddress recipient, PersonName name, Role role) {
        if (!StringUtils.hasText(apiKey)) {
            log.info("Account email skipped: RESEND_API_KEY is not set");
            return;
        }
        var locale = LocaleContextHolder.getLocale();
        var roleName = messageSource.getMessage(roleMessageKey(role), null, locale);
        var subject = messageSource.getMessage("iam.email.account-opened.subject", null, locale);
        var html = messageSource.getMessage("iam.email.account-opened.body",
                new Object[] {HtmlUtils.htmlEscape(name.firstName()), roleName, HtmlUtils.htmlEscape(recipient.value())}, locale);
        var to = StringUtils.hasText(recipientOverride) ? recipientOverride : recipient.value();
        try {
            restClient.post()
                    .uri("/emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("from", sender, "to", List.of(to), "subject", subject, "html", html))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Account email sent through Resend");
        } catch (RestClientException exception) {
            log.warn("Account email could not be sent through Resend: {}", exception.getMessage());
        }
    }

    private static String roleMessageKey(Role role) {
        return "iam.email.role." + role.name().toLowerCase(Locale.ROOT).replace('_', '-');
    }
}
