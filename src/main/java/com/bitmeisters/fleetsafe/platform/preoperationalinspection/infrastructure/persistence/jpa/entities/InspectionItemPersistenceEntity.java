package com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCode;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemName;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemSystem;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Persistence entity for checklist inspection items.
 */
@Entity
@Table(name = "inspection_items")
@Getter
@Setter
@NoArgsConstructor
public class InspectionItemPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "code", nullable = false, unique = true, length = ItemCode.MAX_LENGTH)
    private String code;

    @Column(name = "name", nullable = false, length = ItemName.MAX_LENGTH)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemCategory category;

    @Column(name = "system", nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemSystem system;

    @Column(name = "is_safety_component", nullable = false)
    private boolean safetyComponent;

    @Column(name = "requires_evidence", nullable = false)
    private boolean requiresEvidence;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
