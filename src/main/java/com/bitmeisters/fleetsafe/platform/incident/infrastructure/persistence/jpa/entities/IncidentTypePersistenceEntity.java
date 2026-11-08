package com.bitmeisters.fleetsafe.platform.incident.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "incident_types")
@Getter
@Setter
@NoArgsConstructor
public class IncidentTypePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}