package com.bitmeisters.fleetsafe.platform.iam.application.queryservices;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.queries.GetUserByEmailQuery;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.queries.GetUsersByCompanyIdQuery;

import java.util.List;
import java.util.Optional;

public interface UserQueryService {

    Optional<User> handle(GetUserByIdQuery query);

    Optional<User> handle(GetUserByEmailQuery query);

    List<User> handle(GetUsersByCompanyIdQuery query);
}
