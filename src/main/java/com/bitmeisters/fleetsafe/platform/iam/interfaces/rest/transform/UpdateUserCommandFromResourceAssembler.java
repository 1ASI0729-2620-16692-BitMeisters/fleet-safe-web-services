package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.UpdateUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources.UpdateUserResource;

public class UpdateUserCommandFromResourceAssembler {

    public static UpdateUserCommand toCommandFromResource(User current, UpdateUserResource resource) {
        var name = resource.firstName() == null && resource.lastName() == null ? null : new PersonName(
                resource.firstName() != null ? resource.firstName() : current.getName().firstName(),
                resource.lastName() != null ? resource.lastName() : current.getName().lastName());
        var role = resource.role() == null ? null : Role.valueOf(resource.role());
        return new UpdateUserCommand(current.getId(), name, role, resource.isActive());
    }
}
