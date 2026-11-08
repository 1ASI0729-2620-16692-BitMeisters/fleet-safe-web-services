package com.bitmeisters.fleetsafe.platform.iam.domain.model.commands;

import java.util.UUID;

public record DeactivateUserCommand(UUID userId) {

    public DeactivateUserCommand {
        if (userId == null) throw new IllegalArgumentException("iam.user.error.id.required");
    }
}
