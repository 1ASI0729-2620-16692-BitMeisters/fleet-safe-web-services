package com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.events.UserRegisteredEvent;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private User supervisor() {
        return new User(new RegisterUserCommand(
                new CompanyId(UUID.randomUUID()),
                new EmailAddress("supervisor@transportesandinos.pe"),
                "S3cure!pass",
                new PersonName("Rosa", "Quispe"),
                Role.FLEET_SUPERVISOR), "$2a$10$hash");
    }

    @Test
    void announcesItsRegistration() {
        assertThat(supervisor().domainEvents()).containsExactly(new UserRegisteredEvent(
                new EmailAddress("supervisor@transportesandinos.pe"), new PersonName("Rosa", "Quispe"), Role.FLEET_SUPERVISOR));
    }

    @Test
    void announcesNothingWhenRestoredFromTheDatabase() {
        var user = User.restore(UUID.randomUUID(), new CompanyId(UUID.randomUUID()),
                new EmailAddress("driver@transportesandinos.pe"), "$2a$10$hash", new PersonName("Luis", "Mamani"),
                Role.DRIVER, true, Instant.now(), Instant.now());

        assertThat(user.domainEvents()).isEmpty();
    }

    @Test
    void isActiveWhenRegistered() {
        assertThat(supervisor().isActive()).isTrue();
    }

    @Test
    void keepsOnlyThePasswordHash() {
        assertThat(supervisor().getPasswordHash()).isEqualTo("$2a$10$hash");
    }

    @Test
    void changesItsRole() {
        var user = supervisor();

        user.changeRole(Role.ADMINISTRATOR);

        assertThat(user.getRole()).isEqualTo(Role.ADMINISTRATOR);
    }

    @Test
    void canBeDeactivated() {
        var user = supervisor();

        user.deactivate();

        assertThat(user.isActive()).isFalse();
    }

    @Test
    void keepsItsIdentityAndStateWhenRestored() {
        var id = UUID.randomUUID();
        var created = Instant.parse("2026-10-26T14:00:00Z");

        var user = User.restore(id, new CompanyId(UUID.randomUUID()), new EmailAddress("rosa@andinos.pe"), "$2a$10$hash",
                new PersonName("Rosa", "Quispe"), Role.FLEET_SUPERVISOR, false, created, created);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.isActive()).isFalse();
        assertThat(user.getCreatedAt()).isEqualTo(created);
    }

    @Test
    void rejectsAPasswordShorterThanEightCharacters() {
        assertThatThrownBy(() -> new RegisterUserCommand(
                new CompanyId(UUID.randomUUID()), new EmailAddress("driver@transportesandinos.pe"),
                "short", new PersonName("Luis", "Huamán"), Role.DRIVER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("iam.user.error.password.size");
    }

    @Test
    void cannotBeRegisteredWithoutAPassword() {
        assertThatThrownBy(() -> new RegisterUserCommand(
                new CompanyId(UUID.randomUUID()), new EmailAddress("driver@transportesandinos.pe"),
                " ", new PersonName("Luis", "Huamán"), Role.DRIVER))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("iam.user.error.password.required");
    }
}
