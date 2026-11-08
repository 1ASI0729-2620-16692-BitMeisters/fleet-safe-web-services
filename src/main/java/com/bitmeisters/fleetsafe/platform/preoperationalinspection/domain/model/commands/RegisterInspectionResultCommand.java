package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ResultValue;

/**
 * Command to register an individual checklist evaluation result in an ongoing inspection.
 */
public record RegisterInspectionResultCommand(
        InspectionId inspectionId,
        InspectionItemId itemId,
        ResultValue resultValue) {

    public RegisterInspectionResultCommand {
        if (inspectionId == null) {
            throw new IllegalArgumentException("inspection.error.id.required");
        }
        if (itemId == null) {
            throw new IllegalArgumentException("inspection.item.error.id.required");
        }
        if (resultValue == null) {
            throw new IllegalArgumentException("inspection.result.error.id.required");
        }
    }
}
