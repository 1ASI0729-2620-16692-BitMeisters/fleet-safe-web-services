package com.bitmeisters.fleetsafe.platform.iam.infrastructure.persistence.jpa.adapters;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.EmailAddress;
import com.bitmeisters.fleetsafe.platform.iam.domain.repositories.UserRepository;
import com.bitmeisters.fleetsafe.platform.iam.infrastructure.persistence.jpa.assemblers.UserPersistenceAssembler;
import com.bitmeisters.fleetsafe.platform.iam.infrastructure.persistence.jpa.repositories.UserPersistenceRepository;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserPersistenceRepository userPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public UserRepositoryImpl(UserPersistenceRepository userPersistenceRepository, DomainEventPublisher domainEventPublisher) {
        this.userPersistenceRepository = userPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userPersistenceRepository.findById(id).map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return userPersistenceRepository.findByEmail(email.value()).map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<User> findAllByCompanyId(CompanyId companyId) {
        return userPersistenceRepository.findAllByCompanyIdOrderByLastNameAscFirstNameAsc(companyId.value()).stream()
                .map(UserPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public User save(User user) {
        var saved = userPersistenceRepository.save(UserPersistenceAssembler.toPersistenceFromDomain(user));
        domainEventPublisher.publishAndClearEvents(user);
        return UserPersistenceAssembler.toDomainFromPersistence(saved);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return userPersistenceRepository.existsByEmail(email.value());
    }
}
