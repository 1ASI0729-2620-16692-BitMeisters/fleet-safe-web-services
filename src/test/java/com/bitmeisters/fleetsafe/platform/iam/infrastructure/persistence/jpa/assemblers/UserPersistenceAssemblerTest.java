package com.bitmeisters.fleetsafe.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserPersistenceAssemblerTest {

    @Test
    void keepsEveryFieldWhenGoingToTheDatabaseAndBack() {
        var created = Instant.parse("2026-10-26T14:00:00Z");
        var user = User.restore(UUID.randomUUID(), new CompanyId(UUID.randomUUID()), new EmailAddress("rosa@andinos.pe"),
                "$2a$10$hash", new PersonName("Rosa", "Quispe"), Role.FLEET_SUPERVISOR, false, created, created);

        var restored = UserPersistenceAssembler.toDomainFromPersistence(UserPersistenceAssembler.toPersistenceFromDomain(user));

        assertThat(restored).usingRecursiveComparison().ignoringFields("updatedAt").isEqualTo(user);
    }

    @Test
    void leavesTheIdEmptyForANewUser() {
        var user = User.restore(null, new CompanyId(UUID.randomUUID()), new EmailAddress("luis@andinos.pe"),
                "$2a$10$hash", new PersonName("Luis", "Huamán"), Role.DRIVER, true, null, null);

        var entity = UserPersistenceAssembler.toPersistenceFromDomain(user);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
    }
}
