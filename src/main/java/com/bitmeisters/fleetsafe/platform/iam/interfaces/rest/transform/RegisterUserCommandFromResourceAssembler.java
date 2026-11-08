package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.PersonName;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources.CreateUserResource;

import java.util.UUID;

public class RegisterUserCommandFromResourceAssembler {

    public static RegisterUserCommand toCommandFromResource(CreateUserResource resource, UUID companyId) {
        return new RegisterUserCommand(
                new CompanyId(companyId),
                new EmailAddress(resource.email()),
                resource.password(),
                new PersonName(resource.firstName(), resource.lastName()),
                Role.valueOf(resource.role()));
    }
}
