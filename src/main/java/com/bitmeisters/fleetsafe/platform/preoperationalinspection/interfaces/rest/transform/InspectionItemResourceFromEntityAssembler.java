package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.InspectionItem;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.InspectionItemResource;

/**
 * Assembler to convert InspectionItem domain aggregates into InspectionItemResource response DTOs.
 */
public final class InspectionItemResourceFromEntityAssembler {

    private InspectionItemResourceFromEntityAssembler() {
    }

    /**
     * Converts a domain InspectionItem aggregate into a presentation resource.
     *
     * @param entity the domain aggregate
     * @return the response resource DTO
     */
    public static InspectionItemResource toResourceFromEntity(InspectionItem entity) {
        if (entity == null) {
            return null;
        }
        return new InspectionItemResource(
                entity.getId().value(),
                entity.getCode().value(),
                entity.getName().value(),
                entity.getDescription(),
                entity.getCategory().name(),
                entity.getSystem().name(),
                entity.isSafetyComponent(),
                entity.isRequiresEvidence(),
                entity.getDisplayOrder(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
