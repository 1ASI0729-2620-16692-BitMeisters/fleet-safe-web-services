package com.bitmeisters.fleetsafe.platform.iam.domain.repositories;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(EmailAddress email);

    List<User> findAllByCompanyId(CompanyId companyId);

    User save(User user);

    boolean existsByEmail(EmailAddress email);
}
