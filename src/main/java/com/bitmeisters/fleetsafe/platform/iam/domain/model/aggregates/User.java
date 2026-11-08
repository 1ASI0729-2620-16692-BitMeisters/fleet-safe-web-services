package com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.events.UserRegisteredEvent;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class User extends AbstractDomainAggregateRoot<User> {

    private UUID id;
    private CompanyId companyId;
    private EmailAddress email;
    private String passwordHash;
    private PersonName name;
    private Role role;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    private User(CompanyId companyId, EmailAddress email, String passwordHash, PersonName name, Role role, boolean active) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("iam.user.error.password.required");
        }
        this.companyId = companyId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.role = role;
        this.active = active;
    }

    public User(RegisterUserCommand command, String passwordHash) {
        this(command.companyId(), command.email(), passwordHash, command.name(), command.role(), true);
        registerDomainEvent(new UserRegisteredEvent(command.email(), command.name(), command.role()));
    }

    public static User restore(UUID id, CompanyId companyId, EmailAddress email, String passwordHash,
                               PersonName name, Role role, boolean active, Instant createdAt, Instant updatedAt) {
        var user = new User(companyId, email, passwordHash, name, role, active);
        user.id = id;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        return user;
    }

    public void changeRole(Role role) {
        this.role = role;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public void rename(PersonName name) {
        this.name = name;
    }
}
