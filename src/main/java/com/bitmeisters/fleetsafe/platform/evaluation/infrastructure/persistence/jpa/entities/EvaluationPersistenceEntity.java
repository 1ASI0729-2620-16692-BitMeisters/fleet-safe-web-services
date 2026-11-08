package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "evaluations")
@Getter
@Setter
@NoArgsConstructor
public class EvaluationPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "inspection_id", nullable = false, unique = true)
    private UUID inspectionId;

    @Column(name = "evaluated_at", nullable = false)
    private Instant evaluatedAt;

    @Column(name = "evaluated_by")
    private UUID evaluatedBy;

    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EvaluationDetailPersistenceEntity> details = new ArrayList<>();
}
