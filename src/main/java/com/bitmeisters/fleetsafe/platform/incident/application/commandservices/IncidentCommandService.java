package com.bitmeisters.fleetsafe.platform.incident.application.commandservices;

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
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

public interface IncidentCommandService {

    Result<Incident, IncidentCommandFailure> handle(ReportIncidentCommand command);

    Result<Incident, IncidentCommandFailure> handle(UpdateIncidentCommand command);

    Result<Incident, IncidentCommandFailure> handle(ResolveIncidentCommand command);

    Result<Incident, IncidentCommandFailure> handle(DeleteIncidentCommand command);

    Result<CorrectiveAction, IncidentCommandFailure> handle(
            RegisterCorrectiveActionCommand command);

    Result<Repair, IncidentCommandFailure> handle(ScheduleRepairCommand command);

    Result<Repair, IncidentCommandFailure> handle(CompleteRepairCommand command);

    Result<IncidentFollowUp, IncidentCommandFailure> handle(
            AddIncidentFollowUpCommand command);
}