package com.bitmeisters.fleetsafe.platform.iam.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.AuthenticatedUser;
import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandFailure;
import com.bitmeisters.fleetsafe.platform.iam.application.commandservices.UserCommandService;
import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.bitmeisters.fleetsafe.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.ChangeUserRoleCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.DeactivateUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.SignInCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.UpdateUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.repositories.UserRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;

    public UserCommandServiceImpl(UserRepository userRepository, HashingService hashingService, TokenService tokenService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public Result<User, UserCommandFailure> handle(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            log.warn("Email already registered: companyId={}", command.companyId().value());
            return Result.failure(new UserCommandFailure.EmailAlreadyRegistered());
        }
        var user = userRepository.save(new User(command, hashingService.encode(command.password())));
        log.info("User registered: id={}, companyId={}, role={}", user.getId(), command.companyId().value(), command.role());
        return Result.success(user);
    }

    @Override
    @Transactional
    public Result<User, UserCommandFailure> handle(ChangeUserRoleCommand command) {
        var user = userRepository.findById(command.userId());
        if (user.isEmpty()) {
            log.warn("User not found: id={}", command.userId());
            return Result.failure(new UserCommandFailure.NotFound());
        }
        user.get().changeRole(command.role());
        var updatedUser = userRepository.save(user.get());
        log.info("User role changed: id={}, role={}", updatedUser.getId(), updatedUser.getRole());
        return Result.success(updatedUser);
    }

    @Override
    @Transactional
    public Result<User, UserCommandFailure> handle(DeactivateUserCommand command) {
        var user = userRepository.findById(command.userId());
        if (user.isEmpty()) {
            log.warn("User not found: id={}", command.userId());
            return Result.failure(new UserCommandFailure.NotFound());
        }
        if (!user.get().isActive()) {
            log.warn("User already inactive: id={}", command.userId());
            return Result.failure(new UserCommandFailure.AlreadyInactive());
        }
        user.get().deactivate();
        var deactivatedUser = userRepository.save(user.get());
        log.info("User deactivated: id={}", deactivatedUser.getId());
        return Result.success(deactivatedUser);
    }

    @Override
    public Result<AuthenticatedUser, UserCommandFailure> handle(SignInCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty() || !hashingService.matches(command.password(), user.get().getPasswordHash())) {
            log.warn("Sign in rejected: invalid credentials");
            return Result.failure(new UserCommandFailure.InvalidCredentials());
        }
        if (!user.get().isActive()) {
            log.warn("Sign in rejected: inactive user id={}", user.get().getId());
            return Result.failure(new UserCommandFailure.Inactive());
        }
        var token = tokenService.generateToken(user.get());
        log.info("User signed in: id={}", user.get().getId());
        return Result.success(new AuthenticatedUser(user.get(), token, tokenService.getExpirationInSeconds()));
    }

    @Override
    @Transactional
    public Result<User, UserCommandFailure> handle(UpdateUserCommand command) {
        var user = userRepository.findById(command.userId());
        if (user.isEmpty()) {
            log.warn("User not found: id={}", command.userId());
            return Result.failure(new UserCommandFailure.NotFound());
        }
        if (command.name() != null) user.get().rename(command.name());
        if (command.role() != null) user.get().changeRole(command.role());
        if (Boolean.TRUE.equals(command.active())) user.get().activate();
        if (Boolean.FALSE.equals(command.active())) user.get().deactivate();
        var updatedUser = userRepository.save(user.get());
        log.info("User updated: id={}", updatedUser.getId());
        return Result.success(updatedUser);
    }
}
