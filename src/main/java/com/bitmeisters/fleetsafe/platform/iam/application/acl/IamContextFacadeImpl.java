package com.bitmeisters.fleetsafe.platform.iam.application.acl;

import com.bitmeisters.fleetsafe.platform.iam.application.queryservices.UserQueryService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.bitmeisters.fleetsafe.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class IamContextFacadeImpl implements IamContextFacade {

    private final UserQueryService userQueryService;

    public IamContextFacadeImpl(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @Override
    public Optional<String> fetchFullNameOfUser(UUID userId) {
        if (userId == null) return Optional.empty();
        return userQueryService.handle(new GetUserByIdQuery(userId))
                .map(user -> user.getName().fullName());
    }
}
