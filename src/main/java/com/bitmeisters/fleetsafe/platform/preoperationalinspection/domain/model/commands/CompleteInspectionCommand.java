package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionId;

/**
 * Command to complete a pre-operational inspection and trigger vehicle operational evaluations.
 */
public record CompleteInspectionCommand(InspectionId inspectionId) {

    public CompleteInspectionCommand {
        if (inspectionId == null) {
            throw new IllegalArgumentException("inspection.error.id.required");
        }
    }
}
