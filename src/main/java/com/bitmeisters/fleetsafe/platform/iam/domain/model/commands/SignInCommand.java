package com.bitmeisters.fleetsafe.platform.iam.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;

public record SignInCommand(EmailAddress email, String password) {

    public SignInCommand {
        if (email == null) throw new IllegalArgumentException("iam.user.error.email.required");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("iam.user.error.password.required");
    }
}
