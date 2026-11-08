package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "evaluation_details")
@Getter
@Setter
@NoArgsConstructor
public class EvaluationDetailPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private EvaluationPersistenceEntity evaluation;

    @Column(name = "inspection_result_id", nullable = false)
    private UUID inspectionResultId;

    @Column(name = "evaluation_rule_id", nullable = false)
    private UUID evaluationRuleId;

    @Column(name = "applied_impact", nullable = false)
    @Enumerated(EnumType.STRING)
    private RuleImpact appliedImpact;
}
