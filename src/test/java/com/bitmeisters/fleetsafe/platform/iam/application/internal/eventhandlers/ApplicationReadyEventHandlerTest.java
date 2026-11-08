package com.bitmeisters.fleetsafe.platform.iam.application.internal.eventhandlers;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandFailure;
import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationReadyEventHandlerTest {

    private static final String COMPANY_ID = "1a2b3c4d-5e6f-4a7b-8c9d-0e1f2a3b4c5d";

    @Mock
    private UserCommandService userCommandService;

    @Test
    void registersTheConfiguredAdministrator() {
        when(userCommandService.handle(any(RegisterUserCommand.class)))
                .thenReturn(Result.failure(new UserCommandFailure.EmailAlreadyRegistered()));
        var handler = new ApplicationReadyEventHandler(userCommandService, "Admin@Andinos.pe", "S3cure!pass", COMPANY_ID);

        handler.on(null);

        var command = ArgumentCaptor.forClass(RegisterUserCommand.class);
        verify(userCommandService).handle(command.capture());
        assertThat(command.getValue().email().value()).isEqualTo("admin@andinos.pe");
        assertThat(command.getValue().role()).isEqualTo(Role.ADMINISTRATOR);
        assertThat(command.getValue().companyId().value().toString()).isEqualTo(COMPANY_ID);
    }

    @Test
    void doesNothingWhenTheAdministratorIsNotConfigured() {
        var handler = new ApplicationReadyEventHandler(userCommandService, "admin@andinos.pe", "", COMPANY_ID);

        handler.on(null);

        verify(userCommandService, never()).handle(any(RegisterUserCommand.class));
    }
}
