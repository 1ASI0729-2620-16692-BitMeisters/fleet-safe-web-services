package com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ObservationDescription;

import java.util.UUID;

/**
 * Command to add an observation note to a specific inspection result.
 */
public record AddObservationCommand(
        InspectionId inspectionId,
        InspectionResultId resultId,
        ObservationDescription description,
        UUID createdBy) {

    public AddObservationCommand {
        if (inspectionId == null) {
            throw new IllegalArgumentException("inspection.error.id.required");
        }
        if (resultId == null) {
            throw new IllegalArgumentException("inspection.result.error.id.required");
        }
        if (description == null) {
            throw new IllegalArgumentException("inspection.observation.error.description.required");
        }
        if (createdBy == null) {
            throw new IllegalArgumentException("inspection.observation.error.created-by.required");
        }
    }
}
