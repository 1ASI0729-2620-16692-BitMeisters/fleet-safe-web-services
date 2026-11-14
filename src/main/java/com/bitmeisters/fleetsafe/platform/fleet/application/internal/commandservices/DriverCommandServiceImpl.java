package com.bitmeisters.fleetsafe.platform.fleet.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.DriverCommandFailure;
import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.DriverCommandService;
import com.bitmeisters.fleetsafe.platform.fleet.application.internal.outboundservices.acl.ExternalIamService;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Driver;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.RegisterDriverCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.DriverRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DriverCommandServiceImpl implements DriverCommandService {

    private final DriverRepository driverRepository;
    private final ExternalIamService externalIamService;

    public DriverCommandServiceImpl(DriverRepository driverRepository, ExternalIamService externalIamService) {
        this.driverRepository = driverRepository;
        this.externalIamService = externalIamService;
    }

    @Override
    @Transactional
    public Result<Driver, DriverCommandFailure> handle(RegisterDriverCommand command) {
        if (!externalIamService.userExists(command.userId().value())) {
            log.warn("User not found while registering a driver: userId={}", command.userId().value());
            return Result.failure(new DriverCommandFailure.UserNotFound());
        }
        if (driverRepository.existsByUserId(command.userId())) {
            log.warn("Driver already registered: userId={}", command.userId().value());
            return Result.failure(new DriverCommandFailure.DuplicateDriver());
        }
        var driver = driverRepository.save(new Driver(command));
        log.info("Driver registered: id={}, userId={}, companyId={}",
                driver.getId(), command.userId().value(), command.companyId().value());
        return Result.success(driver);
    }
}
