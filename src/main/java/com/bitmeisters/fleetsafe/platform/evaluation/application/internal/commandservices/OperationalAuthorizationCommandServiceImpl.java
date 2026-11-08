package com.bitmeisters.fleetsafe.platform.evaluation.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.OperationalAuthorizationCommandFailure;
import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.OperationalAuthorizationCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.LiftBlockCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.OperationalAuthorizationRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class OperationalAuthorizationCommandServiceImpl implements OperationalAuthorizationCommandService {

    private final OperationalAuthorizationRepository operationalAuthorizationRepository;

    public OperationalAuthorizationCommandServiceImpl(OperationalAuthorizationRepository operationalAuthorizationRepository) {
        this.operationalAuthorizationRepository = operationalAuthorizationRepository;
    }

    @Override
    @Transactional
    public Result<OperationalAuthorization, OperationalAuthorizationCommandFailure> handle(LiftBlockCommand command) {
        var current = operationalAuthorizationRepository.findFirstByVehicleIdOrderByAuthorizedAtDesc(command.vehicleId());
        if (current.isEmpty()) {
            log.warn("No authorization to lift for vehicleId={}", command.vehicleId().value());
            return Result.failure(new OperationalAuthorizationCommandFailure.NotFound());
        }
        if (!current.get().isBlocked()) {
            log.warn("Vehicle is not blocked: vehicleId={}, status={}", command.vehicleId().value(), current.get().getStatus());
            return Result.failure(new OperationalAuthorizationCommandFailure.NotBlocked());
        }
        var override = operationalAuthorizationRepository.save(new OperationalAuthorization(current.get(), command));
        log.info("Block lifted: vehicleId={}, authorizedBy={}", command.vehicleId().value(), command.authorizedBy());
        return Result.success(override);
    }
}
