package com.bitmeisters.fleetsafe.platform.incident.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.incident.application.commandservices.IncidentCommandFailure;
import com.bitmeisters.fleetsafe.platform.incident.application.commandservices.IncidentCommandService;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.Incident;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.AddIncidentFollowUpCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.CompleteRepairCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.DeleteIncidentCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.RegisterCorrectiveActionCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.ReportIncidentCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.ResolveIncidentCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.ScheduleRepairCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.UpdateIncidentCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.CorrectiveAction;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.IncidentFollowUp;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.Repair;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.CorrectiveActionRepository;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.IncidentFollowUpRepository;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.IncidentRepository;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.IncidentTypeRepository;
import com.bitmeisters.fleetsafe.platform.incident.domain.repositories.RepairRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IncidentCommandServiceImpl implements IncidentCommandService {

    private final IncidentRepository incidentRepository;
    private final IncidentTypeRepository incidentTypeRepository;
    private final CorrectiveActionRepository correctiveActionRepository;
    private final RepairRepository repairRepository;
    private final IncidentFollowUpRepository incidentFollowUpRepository;

    public IncidentCommandServiceImpl(
            IncidentRepository incidentRepository,
            IncidentTypeRepository incidentTypeRepository,
            CorrectiveActionRepository correctiveActionRepository,
            RepairRepository repairRepository,
            IncidentFollowUpRepository incidentFollowUpRepository) {
        this.incidentRepository = incidentRepository;
        this.incidentTypeRepository = incidentTypeRepository;
        this.correctiveActionRepository = correctiveActionRepository;
        this.repairRepository = repairRepository;
        this.incidentFollowUpRepository = incidentFollowUpRepository;
    }

    @Override
    @Transactional
    public Result<Incident, IncidentCommandFailure> handle(ReportIncidentCommand command) {
        if (!incidentTypeRepository.findById(command.incidentTypeId()).isPresent()) {
            return Result.failure(new IncidentCommandFailure.UnknownIncidentType());
        }

        return Result.success(incidentRepository.save(new Incident(command)));
    }

    @Override
    @Transactional
    public Result<Incident, IncidentCommandFailure> handle(UpdateIncidentCommand command) {
        var incidentOptional = incidentRepository.findById(command.incidentId());

        if (incidentOptional.isEmpty()) {
            return Result.failure(new IncidentCommandFailure.NotFound());
        }

        if (!incidentTypeRepository.findById(command.incidentTypeId()).isPresent()) {
            return Result.failure(new IncidentCommandFailure.UnknownIncidentType());
        }

        var incident = incidentOptional.get();
        try {
            incident.update(command);
        } catch (IllegalStateException exception) {
            return Result.failure(new IncidentCommandFailure.InvalidStatus());
        }

        return Result.success(incidentRepository.save(incident));
    }

    @Override
    @Transactional
    public Result<Incident, IncidentCommandFailure> handle(ResolveIncidentCommand command) {
        var incidentOptional = incidentRepository.findById(command.incidentId());

        if (incidentOptional.isEmpty()) {
            return Result.failure(new IncidentCommandFailure.NotFound());
        }

        var incident = incidentOptional.get();
        try {
            incident.resolve(command.resolutionType(), command.resolvedAt());
        } catch (IllegalStateException exception) {
            return Result.failure(new IncidentCommandFailure.InvalidStatus());
        }

        return Result.success(incidentRepository.save(incident));
    }

    @Override
    @Transactional
    public Result<Incident, IncidentCommandFailure> handle(DeleteIncidentCommand command) {
        var incidentOptional = incidentRepository.findById(command.incidentId());

        if (incidentOptional.isEmpty()) {
            return Result.failure(new IncidentCommandFailure.NotFound());
        }

        incidentRepository.delete(incidentOptional.get());

        return Result.success(incidentOptional.get());
    }

    @Override
    @Transactional
    public Result<CorrectiveAction, IncidentCommandFailure> handle(
            RegisterCorrectiveActionCommand command) {
        if (incidentRepository.findById(command.incidentId()).isEmpty()) {
            return Result.failure(new IncidentCommandFailure.NotFound());
        }

        return Result.success(
                correctiveActionRepository.save(new CorrectiveAction(command)));
    }

    @Override
    @Transactional
    public Result<Repair, IncidentCommandFailure> handle(ScheduleRepairCommand command) {
        if (incidentRepository.findById(command.incidentId()).isEmpty()) {
            return Result.failure(new IncidentCommandFailure.NotFound());
        }

        return Result.success(repairRepository.save(new Repair(command)));
    }

    @Override
    @Transactional
    public Result<Repair, IncidentCommandFailure> handle(CompleteRepairCommand command) {
        var repairOptional = repairRepository.findById(command.repairId());

        if (repairOptional.isEmpty()) {
            return Result.failure(new IncidentCommandFailure.NotFound());
        }

        var repair = repairOptional.get();

        if (!repair.getIncidentId().equals(command.incidentId())) {
            return Result.failure(new IncidentCommandFailure.NotFound());
        }

        try {
            repair.complete(command.finishedAt());
        } catch (IllegalStateException exception) {
            return Result.failure(new IncidentCommandFailure.InvalidStatus());
        }

        return Result.success(repairRepository.save(repair));
    }

    @Override
    @Transactional
    public Result<IncidentFollowUp, IncidentCommandFailure> handle(
            AddIncidentFollowUpCommand command) {
        if (incidentRepository.findById(command.incidentId()).isEmpty()) {
            return Result.failure(new IncidentCommandFailure.NotFound());
        }

        return Result.success(
                incidentFollowUpRepository.save(new IncidentFollowUp(command)));
    }
}