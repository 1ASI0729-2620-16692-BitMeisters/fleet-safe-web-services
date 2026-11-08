package com.bitmeisters.fleetsafe.platform.iam.interfaces.acl;

import java.util.Optional;
import java.util.UUID;

public interface IamContextFacade {

    Optional<String> fetchFullNameOfUser(UUID userId);
}
