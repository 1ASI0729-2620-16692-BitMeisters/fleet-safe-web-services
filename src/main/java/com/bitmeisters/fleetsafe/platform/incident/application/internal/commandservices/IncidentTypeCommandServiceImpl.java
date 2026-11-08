package com.bitmeisters.fleetsafe.platform.incident.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.incident.application.commandservices.IncidentTypeCommandFailure;
import com.bitmeisters.fleetsafe.platform.incident.application.commandservices.IncidentTypeCommandService;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.IncidentType;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.RegisterIncidentTypeCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.UpdateIncidentTypeCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.IncidentTypeRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidentTypeCommandServiceImpl implements IncidentTypeCommandService {

    private final IncidentTypeRepository incidentTypeRepository;

    public IncidentTypeCommandServiceImpl(IncidentTypeRepository incidentTypeRepository) {
        this.incidentTypeRepository = incidentTypeRepository;
    }

    @Override
    @Transactional
    public Result<IncidentType, IncidentTypeCommandFailure> handle(
            RegisterIncidentTypeCommand command) {
        if (incidentTypeRepository.existsByCode(command.code())) {
            return Result.failure(new IncidentTypeCommandFailure.DuplicateCode());
        }

        var incidentType = incidentTypeRepository.save(new IncidentType(command));

        return Result.success(incidentType);
    }

    @Override
    @Transactional
    public Result<IncidentType, IncidentTypeCommandFailure> handle(
            UpdateIncidentTypeCommand command) {
        var incidentTypeOptional = incidentTypeRepository.findById(command.incidentTypeId());

        if (incidentTypeOptional.isEmpty()) {
            return Result.failure(new IncidentTypeCommandFailure.NotFound());
        }

        var incidentType = incidentTypeOptional.get();

        if (!incidentType.getCode().equals(command.code())
                && incidentTypeRepository.existsByCode(command.code())) {
            return Result.failure(new IncidentTypeCommandFailure.DuplicateCode());
        }

        incidentType.update(command);

        return Result.success(incidentTypeRepository.save(incidentType));
    }
}