package com.bitmeisters.fleetsafe.platform.incident.domain.model.entities;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.RegisterCorrectiveActionCommand;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class CorrectiveAction {

    private UUID id;
    private UUID incidentId;
    private String description;
    private UUID performedBy;
    private Instant performedAt;
    private String evidenceUrl;

    public CorrectiveAction(RegisterCorrectiveActionCommand command) {
        this.incidentId = command.incidentId();
        this.description = command.description();
        this.performedBy = command.performedBy();
        this.performedAt = command.performedAt();
        this.evidenceUrl = command.evidenceUrl();
    }

    public static CorrectiveAction restore(
            UUID id,
            UUID incidentId,
            String description,
            UUID performedBy,
            Instant performedAt,
            String evidenceUrl) {

        var action = new CorrectiveAction(
                new RegisterCorrectiveActionCommand(
                        incidentId,
                        description,
                        performedBy,
                        performedAt,
                        evidenceUrl));

        action.id = id;
        return action;
    }
}