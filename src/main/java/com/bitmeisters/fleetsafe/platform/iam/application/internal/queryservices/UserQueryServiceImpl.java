package com.bitmeisters.fleetsafe.platform.iam.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.iam.application.queryservices.UserQueryService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.queries.GetUserByEmailQuery;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.queries.GetUsersByCompanyIdQuery;
import com.bitmeisters.fleetsafe.platform.iam.domain.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    public UserQueryServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> handle(GetUserByIdQuery query) {
        var result = userRepository.findById(query.userId());
        if (result.isEmpty()) log.debug("No user found for id: {}", query.userId());
        return result;
    }

    @Override
    public Optional<User> handle(GetUserByEmailQuery query) {
        return userRepository.findByEmail(query.email());
    }

    @Override
    public List<User> handle(GetUsersByCompanyIdQuery query) {
        var results = userRepository.findAllByCompanyId(query.companyId()).stream()
                .filter(user -> query.role() == null || user.getRole() == query.role())
                .filter(user -> query.active() == null || user.isActive() == query.active())
                .toList();
        log.debug("Found {} users for companyId: {}", results.size(), query.companyId().value());
        return results;
    }
}
