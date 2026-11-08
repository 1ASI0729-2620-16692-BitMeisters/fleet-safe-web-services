package com.bitmeisters.fleetsafe.platform.iam;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandFailure;
import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandService;
import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.bitmeisters.fleetsafe.platform.iam.application.queryservices.UserQueryService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.ChangeUserRoleCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.DeactivateUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.SignInCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.queries.GetUserByEmailQuery;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.queries.GetUsersByCompanyIdQuery;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserFlowIntegrationTest {

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private UserQueryService userQueryService;

    @Autowired
    private HashingService hashingService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void registersFindsAndManagesAUser() {
        var company = new CompanyId(UUID.randomUUID());
        var registered = userCommandService.handle(new RegisterUserCommand(
                company, new EmailAddress("Rosa.Quispe@Andinos.pe"), "S3cure!pass",
                new PersonName("Rosa", "Quispe"), Role.FLEET_SUPERVISOR)).success().orElseThrow();

        assertThat(registered.getId()).isNotNull();
        assertThat(registered.getPasswordHash()).isNotEqualTo("S3cure!pass");
        assertThat(hashingService.matches("S3cure!pass", registered.getPasswordHash())).isTrue();
        assertThat(registered.getCreatedAt()).isNotNull();
        assertThat(userQueryService.handle(new GetUserByEmailQuery(new EmailAddress("rosa.quispe@andinos.pe"))))
                .get().extracting(User::getId).isEqualTo(registered.getId());
        assertThat(userQueryService.handle(new GetUsersByCompanyIdQuery(company))).hasSize(1);

        var signedIn = userCommandService.handle(new SignInCommand(new EmailAddress("ROSA.QUISPE@andinos.pe"), "S3cure!pass"))
                .success().orElseThrow();
        assertThat(tokenService.validateToken(signedIn.token())).isTrue();
        assertThat(tokenService.getUserIdFromToken(signedIn.token())).isEqualTo(registered.getId().toString());

        var duplicate = userCommandService.handle(new RegisterUserCommand(
                new CompanyId(UUID.randomUUID()), new EmailAddress("rosa.quispe@andinos.pe"), "Another!pass",
                new PersonName("Otra", "Persona"), Role.DRIVER));
        assertThat(duplicate.failure()).get().isInstanceOf(UserCommandFailure.EmailAlreadyRegistered.class);

        userCommandService.handle(new ChangeUserRoleCommand(registered.getId(), Role.ADMINISTRATOR));
        userCommandService.handle(new DeactivateUserCommand(registered.getId()));
        entityManager.flush();
        entityManager.clear();

        var stored = userQueryService.handle(new GetUserByIdQuery(registered.getId())).orElseThrow();
        assertThat(stored.getRole()).isEqualTo(Role.ADMINISTRATOR);
        assertThat(stored.isActive()).isFalse();
        assertThat(stored.getCreatedAt()).isEqualTo(registered.getCreatedAt());
        assertThat(userCommandService.handle(new SignInCommand(new EmailAddress("rosa.quispe@andinos.pe"), "S3cure!pass")).failure())
                .get().isInstanceOf(UserCommandFailure.Inactive.class);
    }
}
