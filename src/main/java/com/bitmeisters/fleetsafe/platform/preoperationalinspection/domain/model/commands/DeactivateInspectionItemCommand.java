package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;

/**
 * Command to deactivate an inspection item without deleting it from the database.
 */
public record DeactivateInspectionItemCommand(InspectionItemId id) {

    public DeactivateInspectionItemCommand {
        if (id == null) {
            throw new IllegalArgumentException("inspection.item.error.id.required");
        }
    }
}