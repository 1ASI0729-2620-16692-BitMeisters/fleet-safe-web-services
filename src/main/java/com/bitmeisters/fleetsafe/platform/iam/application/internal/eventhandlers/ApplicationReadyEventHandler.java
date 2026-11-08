package com.bitmeisters.fleetsafe.platform.iam.application.internal.eventhandlers;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Slf4j
@Service
public class ApplicationReadyEventHandler {

    private final UserCommandService userCommandService;
    private final String administratorEmail;
    private final String administratorPassword;
    private final String administratorCompanyId;

    public ApplicationReadyEventHandler(UserCommandService userCommandService,
                                        @Value("${iam.administrator.email:}") String administratorEmail,
                                        @Value("${iam.administrator.password:}") String administratorPassword,
                                        @Value("${iam.administrator.company-id:}") String administratorCompanyId) {
        this.userCommandService = userCommandService;
        this.administratorEmail = administratorEmail;
        this.administratorPassword = administratorPassword;
        this.administratorCompanyId = administratorCompanyId;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        if (!StringUtils.hasText(administratorEmail) || !StringUtils.hasText(administratorPassword)
                || !StringUtils.hasText(administratorCompanyId)) {
            log.info("Administrator seeding skipped: ADMIN_EMAIL, ADMIN_PASSWORD and ADMIN_COMPANY_ID are not all set");
            return;
        }
        var command = new RegisterUserCommand(
                new CompanyId(UUID.fromString(administratorCompanyId)),
                new EmailAddress(administratorEmail),
                administratorPassword,
                new PersonName("Platform", "Administrator"),
                Role.ADMINISTRATOR);
        userCommandService.handle(command).fold(
                administrator -> {
                    log.info("Administrator seeded: id={}", administrator.getId());
                    return administrator;
                },
                failure -> {
                    log.info("Administrator seeding not needed: {}", failure.messageKey());
                    return null;
                });
    }
}
