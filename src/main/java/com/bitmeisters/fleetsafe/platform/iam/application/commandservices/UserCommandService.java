package com.bitmeisters.fleetsafe.platform.iam.application.commandservices;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.aggregates.User;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.ChangeUserRoleCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.DeactivateUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.RegisterUserCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.SignInCommand;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.commands.UpdateUserCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

public interface UserCommandService {

    Result<User, UserCommandFailure> handle(RegisterUserCommand command);

    Result<User, UserCommandFailure> handle(ChangeUserRoleCommand command);

    Result<User, UserCommandFailure> handle(DeactivateUserCommand command);

    Result<AuthenticatedUser, UserCommandFailure> handle(SignInCommand command);

    Result<User, UserCommandFailure> handle(UpdateUserCommand command);
}
