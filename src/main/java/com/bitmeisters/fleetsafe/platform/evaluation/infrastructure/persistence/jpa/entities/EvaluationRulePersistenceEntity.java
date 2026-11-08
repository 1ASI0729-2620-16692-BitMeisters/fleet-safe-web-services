package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "evaluation_rules")
@Getter
@Setter
@NoArgsConstructor
public class EvaluationRulePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "inspection_item_id", nullable = false)
    private UUID inspectionItemId;

    @Column(name = "name", nullable = false, length = RuleName.MAX_LENGTH)
    private String name;

    @Column(name = "found_result", nullable = false)
    @Enumerated(EnumType.STRING)
    private ResultValue foundResult;

    @Column(name = "resulting_impact", nullable = false)
    @Enumerated(EnumType.STRING)
    private RuleImpact resultingImpact;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active;
}
