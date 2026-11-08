package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.CreateInspectionItemCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCode;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemName;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemSystem;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.CreateInspectionItemResource;

/**
 * Assembler to convert CreateInspectionItemResource into CreateInspectionItemCommand.
 */
public final class CreateInspectionItemCommandFromResourceAssembler {

    private CreateInspectionItemCommandFromResourceAssembler() {
    }

    /**
     * Converts a CreateInspectionItemResource DTO into domain CreateInspectionItemCommand.
     *
     * @param resource the request DTO
     * @return the domain command
     */
    public static CreateInspectionItemCommand toCommandFromResource(CreateInspectionItemResource resource) {
        return new CreateInspectionItemCommand(
                new ItemCode(resource.code()),
                new ItemName(resource.name()),
                resource.description(),
                ItemCategory.valueOf(resource.category()),
                ItemSystem.valueOf(resource.system()),
                resource.isSafetyComponent(),
                resource.requiresEvidence(),
                resource.displayOrder());
    }
}
