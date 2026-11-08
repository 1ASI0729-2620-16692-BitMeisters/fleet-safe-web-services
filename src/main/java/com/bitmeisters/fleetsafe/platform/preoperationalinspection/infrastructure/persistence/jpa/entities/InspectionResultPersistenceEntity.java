package com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.entities;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemName;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA Persistence entity for checklist evaluation results within an inspection.
 */
@Entity
@Table(name = "inspection_results")
@Getter
@Setter
@NoArgsConstructor
public class InspectionResultPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspection_id", nullable = false)
    private InspectionPersistenceEntity inspection;

    @Column(name = "inspection_item_id", nullable = false)
    private UUID inspectionItemId;

    @Column(name = "item_name", nullable = false, length = ItemName.MAX_LENGTH)
    private String itemName;

    @Column(name = "item_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemCategory itemCategory;

    @Column(name = "result_value", nullable = false)
    @Enumerated(EnumType.STRING)
    private ResultValue resultValue;

    @OneToMany(mappedBy = "inspectionResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ObservationPersistenceEntity> observations = new ArrayList<>();
}
