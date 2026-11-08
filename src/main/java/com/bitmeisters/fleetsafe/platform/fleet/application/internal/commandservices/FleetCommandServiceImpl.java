package com.bitmeisters.fleetsafe.platform.fleet.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.FleetCommandFailure;
import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.FleetCommandService;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Fleet;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.CreateFleetCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.CompanyRepository;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.FleetRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class FleetCommandServiceImpl implements FleetCommandService {

    private final FleetRepository fleetRepository;
    private final CompanyRepository companyRepository;

    public FleetCommandServiceImpl(FleetRepository fleetRepository, CompanyRepository companyRepository) {
        this.fleetRepository = fleetRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public Result<Fleet, FleetCommandFailure> handle(CreateFleetCommand command) {
        if (!companyRepository.existsById(command.companyId())) {
            log.warn("Company not registered while creating a fleet: companyId={}", command.companyId().value());
            return Result.failure(new FleetCommandFailure.CompanyNotRegistered());
        }
        var fleet = fleetRepository.save(new Fleet(command));
        log.info("Fleet created: id={}, companyId={}", fleet.getId(), command.companyId().value());
        return Result.success(fleet);
    }
}
