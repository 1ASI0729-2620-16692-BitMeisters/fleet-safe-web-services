package com.bitmeisters.fleetsafe.platform.incident.application.commandservices;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.IncidentType;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.RegisterIncidentTypeCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.UpdateIncidentTypeCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

public interface IncidentTypeCommandService {

    Result<IncidentType, IncidentTypeCommandFailure> handle(
            RegisterIncidentTypeCommand command);

    Result<IncidentType, IncidentTypeCommandFailure> handle(
            UpdateIncidentTypeCommand command);
}