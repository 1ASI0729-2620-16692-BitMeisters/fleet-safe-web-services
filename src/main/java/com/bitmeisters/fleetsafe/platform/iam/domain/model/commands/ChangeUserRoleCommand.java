package com.bitmeisters.fleetsafe.platform.iam.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;

import java.util.UUID;

public record ChangeUserRoleCommand(UUID userId, Role role) {

    public ChangeUserRoleCommand {
        if (userId == null) throw new IllegalArgumentException("iam.user.error.id.required");
        if (role == null) throw new IllegalArgumentException("iam.user.error.role.required");
    }
}
