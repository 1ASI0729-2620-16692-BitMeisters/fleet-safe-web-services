package com.bitmeisters.fleetsafe.platform.iam.application.internal.eventhandlers;

import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.email.EmailService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.events.UserRegisteredEvent;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserRegisteredEventHandlerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserRegisteredEventHandler handler;

    @Test
    void emailsTheNewUser() {
        var email = new EmailAddress("luis@transportesandinos.pe");
        var name = new PersonName("Luis", "Mamani");

        handler.on(new UserRegisteredEvent(email, name, Role.DRIVER));

        verify(emailService).sendAccountOpenedEmail(email, name, Role.DRIVER);
    }
}
