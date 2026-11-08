package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources.UserResource;

public class UserResourceFromEntityAssembler {

    public static UserResource toResourceFromEntity(User entity) {
        return new UserResource(
                entity.getId(),
                entity.getCompanyId().value(),
                entity.getEmail().value(),
                entity.getName().firstName(),
                entity.getName().lastName(),
                entity.getRole().name(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
