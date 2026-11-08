package com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.AuthenticatedUser;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {

    public static AuthenticatedUserResource toResourceFromEntity(AuthenticatedUser authenticatedUser) {
        return new AuthenticatedUserResource(
                authenticatedUser.token(),
                "Bearer",
                authenticatedUser.expiresInSeconds(),
                UserResourceFromEntityAssembler.toResourceFromEntity(authenticatedUser.user()));
    }
}
