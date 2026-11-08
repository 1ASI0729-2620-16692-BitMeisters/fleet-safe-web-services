package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.UpdateInspectionItemCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemName;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemSystem;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.UpdateInspectionItemResource;

import java.util.UUID;

/**
 * Assembler to convert UpdateInspectionItemResource and UUID into UpdateInspectionItemCommand.
 */
public final class UpdateInspectionItemCommandFromResourceAssembler {

    private UpdateInspectionItemCommandFromResourceAssembler() {
    }

    /**
     * Converts a target UUID and UpdateInspectionItemResource DTO into domain UpdateInspectionItemCommand.
     *
     * @param id       the unique identifier of the item to update
     * @param resource the update request DTO
     * @return the domain command
     */
    public static UpdateInspectionItemCommand toCommandFromResource(UUID id, UpdateInspectionItemResource resource) {
        return new UpdateInspectionItemCommand(
                new InspectionItemId(id),
                new ItemName(resource.name()),
                resource.description(),
                ItemCategory.valueOf(resource.category()),
                ItemSystem.valueOf(resource.system()),
                resource.isSafetyComponent(),
                resource.requiresEvidence(),
                resource.displayOrder());
    }
}
