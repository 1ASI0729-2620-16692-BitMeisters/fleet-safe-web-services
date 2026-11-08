package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCategory;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemCode;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemName;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemSystem;

/**
 * Command to create a new inspection item in the catalog.
 */
public record CreateInspectionItemCommand(
        ItemCode code,
        ItemName name,
        String description,
        ItemCategory category,
        ItemSystem system,
        boolean safetyComponent,
        boolean requiresEvidence,
        int displayOrder) {

    public CreateInspectionItemCommand {
        if (code == null) {
            throw new IllegalArgumentException("inspection.item.error.code.required");
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