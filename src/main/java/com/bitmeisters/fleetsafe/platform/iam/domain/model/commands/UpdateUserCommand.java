package com.bitmeisters.fleetsafe.platform.iam.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;

import java.util.UUID;

public record UpdateUserCommand(UUID userId, PersonName name, Role role, Boolean active) {

    public UpdateUserCommand {
        if (userId == null) throw new IllegalArgumentException("iam.user.error.id.required");
        if (name == null && role == null && active == null) throw new IllegalArgumentException("iam.user.error.update.empty");
    }
}
