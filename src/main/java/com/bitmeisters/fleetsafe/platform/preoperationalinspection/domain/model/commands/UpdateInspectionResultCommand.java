package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ResultValue;

/**
 * Command to update an evaluation result while the inspection is still in progress.
 */
public record UpdateInspectionResultCommand(
        InspectionId inspectionId,
        InspectionResultId resultId,
        ResultValue resultValue) {

    public UpdateInspectionResultCommand {
        if (inspectionId == null) {
            throw new IllegalArgumentException("inspection.error.id.required");
        }
        if (resultId == null) {
            throw new IllegalArgumentException("inspection.result.error.id.required");
        }
        if (resultValue == null) {
            throw new IllegalArgumentException("inspection.result.error.id.required");
        }
    }
}
