package com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.RegisterIncidentTypeCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.UpdateIncidentTypeCommand;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.valueobjects.Timestamps;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class IncidentType extends AbstractDomainAggregateRoot<IncidentType> {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public IncidentType(RegisterIncidentTypeCommand command) {
        this.code = command.code();
        this.name = command.name();
        this.description = command.description();
        this.active = true;
        this.createdAt = Timestamps.now();
        this.updatedAt = this.createdAt;
    }

    public static IncidentType restore(
            UUID id,
            String code,
            String name,
            String description,
            boolean active,
            Instant createdAt,
            Instant updatedAt) {

        var incidentType = new IncidentType(
                new RegisterIncidentTypeCommand(code, name, description));

        incidentType.id = id;
        incidentType.active = active;
        incidentType.createdAt = createdAt;
        incidentType.updatedAt = updatedAt;

        return incidentType;
    }

    public void update(UpdateIncidentTypeCommand command) {
        this.code = command.code();
        this.name = command.name();
        this.description = command.description();

        if (command.active() != null) {
            this.active = command.active();
        }

        this.updatedAt = Timestamps.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = Timestamps.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = Timestamps.now();
    }

    public boolean isActive() {
        return active;
    }
}