package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.SignInCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {

    public static SignInCommand toCommandFromResource(SignInResource resource) {
        return new SignInCommand(new EmailAddress(resource.email()), resource.password());
    }
}
