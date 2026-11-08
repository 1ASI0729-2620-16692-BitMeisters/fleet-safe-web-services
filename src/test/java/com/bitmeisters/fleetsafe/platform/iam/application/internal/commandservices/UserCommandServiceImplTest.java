package com.bitmeisters.fleetsafe.platform.iam.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandFailure;
import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.AuthenticatedUser;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.ChangeUserRoleCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.DeactivateUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.SignInCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import com.bitmeisters.fleetsafe.platform.iam.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashingService hashingService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    private final RegisterUserCommand registerSupervisor = new RegisterUserCommand(
            new CompanyId(UUID.randomUUID()),
            new EmailAddress("supervisor@transportesandinos.pe"),
            "S3cure!pass",
            new PersonName("Rosa", "Quispe"),
            Role.FLEET_SUPERVISOR);

    @Test
    void registersAUserWithANewEmailAndKeepsOnlyTheHashOfItsPassword() {
        when(userRepository.existsByEmail(registerSupervisor.email())).thenReturn(false);
        when(hashingService.encode("S3cure!pass")).thenReturn("$2a$10$hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = userCommandService.handle(registerSupervisor);

        assertThat(result.success()).get().extracting(User::getRole).isEqualTo(Role.FLEET_SUPERVISOR);
        assertThat(result.success()).get().extracting(User::getPasswordHash).isEqualTo("$2a$10$hash");
    }

    @Test
    void rejectsAnEmailThatIsAlreadyRegistered() {
        when(userRepository.existsByEmail(registerSupervisor.email())).thenReturn(true);

        var result = userCommandService.handle(registerSupervisor);

        assertThat(result.failure()).get().isInstanceOf(UserCommandFailure.EmailAlreadyRegistered.class);
        verify(userRepository, never()).save(any());
        verify(hashingService, never()).encode(any());
    }

    @Test
    void changesTheRoleOfAnExistingUser() {
        var id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(new User(registerSupervisor, "$2a$10$hash")));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = userCommandService.handle(new ChangeUserRoleCommand(id, Role.ADMINISTRATOR));

        assertThat(result.success()).get().extracting(User::getRole).isEqualTo(Role.ADMINISTRATOR);
    }

    @Test
    void reportsAUserThatDoesNotExist() {
        var id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        var result = userCommandService.handle(new ChangeUserRoleCommand(id, Role.DRIVER));

        assertThat(result.failure()).get().isInstanceOf(UserCommandFailure.NotFound.class);
    }

    @Test
    void rejectsDeactivatingAUserTwice() {
        var user = new User(registerSupervisor, "$2a$10$hash");
        user.deactivate();
        var id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        var result = userCommandService.handle(new DeactivateUserCommand(id));

        assertThat(result.failure()).get().isInstanceOf(UserCommandFailure.AlreadyInactive.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void signsInWithTheRightPassword() {
        var user = new User(registerSupervisor, "$2a$10$hash");
        when(userRepository.findByEmail(registerSupervisor.email())).thenReturn(Optional.of(user));
        when(hashingService.matches("S3cure!pass", "$2a$10$hash")).thenReturn(true);
        when(tokenService.generateToken(user)).thenReturn("jwt");
        when(tokenService.getExpirationInSeconds()).thenReturn(3600L);

        var result = userCommandService.handle(new SignInCommand(registerSupervisor.email(), "S3cure!pass"));

        assertThat(result.success()).get().isEqualTo(new AuthenticatedUser(user, "jwt", 3600L));
    }

    @Test
    void answersTheSameForAnUnknownEmailAndForAWrongPassword() {
        when(userRepository.findByEmail(registerSupervisor.email())).thenReturn(Optional.empty());
        var unknownEmail = userCommandService.handle(new SignInCommand(registerSupervisor.email(), "S3cure!pass"));

        when(userRepository.findByEmail(registerSupervisor.email())).thenReturn(Optional.of(new User(registerSupervisor, "$2a$10$hash")));
        when(hashingService.matches("Wrong!pass", "$2a$10$hash")).thenReturn(false);
        var wrongPassword = userCommandService.handle(new SignInCommand(registerSupervisor.email(), "Wrong!pass"));

        assertThat(unknownEmail.failure()).get().isInstanceOf(UserCommandFailure.InvalidCredentials.class);
        assertThat(wrongPassword.failure()).get().isInstanceOf(UserCommandFailure.InvalidCredentials.class);
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void doesNotSignInAnInactiveUser() {
        var user = new User(registerSupervisor, "$2a$10$hash");
        user.deactivate();
        when(userRepository.findByEmail(registerSupervisor.email())).thenReturn(Optional.of(user));
        when(hashingService.matches("S3cure!pass", "$2a$10$hash")).thenReturn(true);

        var result = userCommandService.handle(new SignInCommand(registerSupervisor.email(), "S3cure!pass"));

        assertThat(result.failure()).get().isInstanceOf(UserCommandFailure.Inactive.class);
        verify(tokenService, never()).generateToken(any());
    }
}
