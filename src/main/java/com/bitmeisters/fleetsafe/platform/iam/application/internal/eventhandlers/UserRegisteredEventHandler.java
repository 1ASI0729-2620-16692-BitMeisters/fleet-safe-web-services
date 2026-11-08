package com.bitmeisters.fleetsafe.platform.iam.application.internal.eventhandlers;

import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.email.EmailService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.events.UserRegisteredEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class UserRegisteredEventHandler {

    private final EmailService emailService;

    public UserRegisteredEventHandler(EmailService emailService) {
        this.emailService = emailService;
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void on(UserRegisteredEvent event) {
        emailService.sendAccountOpenedEmail(event.email(), event.name(), event.role());
    }
}
