package com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.InspectionItem;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCode;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemName;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.infrastructure.persistence.jpa.entities.InspectionItemPersistenceEntity;

/**
 * Assembler to map between the InspectionItem domain aggregate and the JPA persistence entity.
 */
public final class InspectionItemPersistenceAssembler {

    private InspectionItemPersistenceAssembler() {
    }

    /**
     * Converts a JPA persistence entity into a rich domain aggregate.
     *
     * @param entity the persistence entity
     * @return reconstituted domain aggregate
     */
    public static InspectionItem toDomainFromPersistence(InspectionItemPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }
        return InspectionItem.restore(
                new InspectionItemId(entity.getId()),
                new ItemCode(entity.getCode()),
                new ItemName(entity.getName()),
                entity.getDescription(),
                entity.getCategory(),
                entity.getSystem(),
                entity.isSafetyComponent(),
                entity.isRequiresEvidence(),
                entity.getDisplayOrder(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Converts a domain aggregate into a JPA persistence entity.
     *
     * @param item the domain aggregate
     * @return JPA persistence entity ready for storage
     */
    public static InspectionItemPersistenceEntity toPersistenceFromDomain(InspectionItem item) {
        if (item == null) {
            return null;
        }
        var entity = new InspectionItemPersistenceEntity();
        if (item.getId() != null) {
            entity.setId(item.getId().value());
            entity.setCreatedAt(item.getCreatedAt());
        }
        entity.setCode(item.getCode().value());
        entity.setName(item.getName().value());
        entity.setDescription(item.getDescription());
        entity.setCategory(item.getCategory());
        entity.setSystem(item.getSystem());
        entity.setSafetyComponent(item.isSafetyComponent());
        entity.setRequiresEvidence(item.isRequiresEvidence());
        entity.setDisplayOrder(item.getDisplayOrder());
        entity.setActive(item.isActive());
        return entity;
    }
}
