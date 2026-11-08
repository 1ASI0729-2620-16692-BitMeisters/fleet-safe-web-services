package com.bitmeisters.fleetsafe.platform.iam.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;

public record RegisterUserCommand(
        CompanyId companyId,
        EmailAddress email,
        String password,
        PersonName name,
        Role role) {

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 72;

    public RegisterUserCommand {
        if (companyId == null) throw new IllegalArgumentException("iam.user.error.company-id.required");
        if (email == null) throw new IllegalArgumentException("iam.user.error.email.required");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("iam.user.error.password.required");
        if (password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) throw new IllegalArgumentException("iam.user.error.password.size");
        if (name == null) throw new IllegalArgumentException("iam.user.error.first-name.required");
        if (role == null) throw new IllegalArgumentException("iam.user.error.role.required");
    }
}
