package com.bitmeisters.fleetsafe.platform.incident.application.internal.outboundservices.acl;

import com.bitmeisters.fleetsafe.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public String nameOf(UUID userId) {
        return iamContextFacade.fetchFullNameOfUser(userId).orElse(null);
    }
}
