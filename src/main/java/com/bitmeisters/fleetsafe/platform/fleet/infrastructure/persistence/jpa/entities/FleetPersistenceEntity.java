package com.bitmeisters.fleetsafe.platform.fleet.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.CreateFleetCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.FleetName;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "fleets")
@Getter
@Setter
@NoArgsConstructor
public class FleetPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "name", nullable = false, length = FleetName.MAX_LENGTH)
    private String name;

    @Column(name = "description", length = CreateFleetCommand.DESCRIPTION_MAX_LENGTH)
    private String description;
}
