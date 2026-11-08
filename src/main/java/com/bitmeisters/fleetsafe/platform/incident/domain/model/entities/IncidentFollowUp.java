package com.bitmeisters.fleetsafe.platform.incident.domain.model.entities;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.AddIncidentFollowUpCommand;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class IncidentFollowUp {

    private UUID id;
    private UUID incidentId;
    private String note;
    private UUID createdBy;
    private Instant createdAt;

    public IncidentFollowUp(AddIncidentFollowUpCommand command) {
        this.incidentId = command.incidentId();
        this.note = command.note();
        this.createdBy = command.createdBy();
        this.createdAt = command.createdAt();
    }

    public static IncidentFollowUp restore(
            UUID id,
            UUID incidentId,
            String note,
            UUID createdBy,
            Instant createdAt) {

        var followUp = new IncidentFollowUp(
                new AddIncidentFollowUpCommand(
                        incidentId,
                        note,
                        createdBy,
                        createdAt));

        followUp.id = id;
        return followUp;
    }
}