package com.bitmeisters.fleetsafe.platform.fleet.application.internal.outboundservices.acl;

import com.bitmeisters.fleetsafe.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Anti-corruption layer towards Identity and Access, which keeps the user accounts of the drivers.
 */
@Service
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public boolean userExists(UUID userId) {
        return iamContextFacade.fetchFullNameOfUser(userId).isPresent();
    }
}
