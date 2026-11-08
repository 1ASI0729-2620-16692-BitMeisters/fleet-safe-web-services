package com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.CreateFleetCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.FleetName;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * A group of vehicles that a company operates together. A fleet cannot exist outside its company,
 * which it references by identifier.
 */
@Getter
public class Fleet extends AbstractDomainAggregateRoot<Fleet> {

    private UUID id;
    private CompanyId companyId;
    private FleetName name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    private Fleet() {
    }

    public Fleet(CreateFleetCommand command) {
        this.companyId = command.companyId();
        this.name = command.name();
        this.description = command.description();
    }

    public static Fleet restore(UUID id, CompanyId companyId, FleetName name, String description,
                                Instant createdAt, Instant updatedAt) {
        var fleet = new Fleet();
        fleet.id = id;
        fleet.companyId = companyId;
        fleet.name = name;
        fleet.description = description;
        fleet.createdAt = createdAt;
        fleet.updatedAt = updatedAt;
        return fleet;
    }
}
