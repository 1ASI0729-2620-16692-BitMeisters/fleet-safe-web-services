package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemName;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemSystem;

/**
 * Command to update an existing inspection item in the catalog.
 */
public record UpdateInspectionItemCommand(
        InspectionItemId id,
        ItemName name,
        String description,
        ItemCategory category,
        ItemSystem system,
        boolean safetyComponent,
        boolean requiresEvidence,
        int displayOrder) {

    public UpdateInspectionItemCommand {
        if (id == null) {
            throw new IllegalArgumentException("inspection.item.error.id.required");
        }
        if (name == null) {
            throw new IllegalArgumentException("inspection.item.error.name.required");
        }
        if (category == null) {
            throw new IllegalArgumentException("inspection.item.error.category.required");
        }
        if (system == null) {
            throw new IllegalArgumentException("inspection.item.error.system.required");
        }
    }
}