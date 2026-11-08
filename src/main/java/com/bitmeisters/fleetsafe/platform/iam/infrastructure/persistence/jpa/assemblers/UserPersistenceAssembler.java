package com.bitmeisters.fleetsafe.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;

public final class UserPersistenceAssembler {

    private UserPersistenceAssembler() {
    }

    public static User toDomainFromPersistence(UserPersistenceEntity entity) {
        if (entity == null) return null;
        return User.restore(
                entity.getId(),
                new CompanyId(entity.getCompanyId()),
                new EmailAddress(entity.getEmail()),
                entity.getPasswordHash(),
                new PersonName(entity.getFirstName(), entity.getLastName()),
                entity.getRole(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static UserPersistenceEntity toPersistenceFromDomain(User user) {
        if (user == null) return null;
        var entity = new UserPersistenceEntity();
        if (user.getId() != null) {
            entity.setId(user.getId());
            entity.setCreatedAt(user.getCreatedAt());
        }
        entity.setCompanyId(user.getCompanyId().value());
        entity.setEmail(user.getEmail().value());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setFirstName(user.getName().firstName());
        entity.setLastName(user.getName().lastName());
        entity.setRole(user.getRole());
        entity.setActive(user.isActive());
        return entity;
    }
}
