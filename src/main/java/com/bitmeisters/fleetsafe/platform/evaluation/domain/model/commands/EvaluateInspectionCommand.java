package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultSnapshot;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;

import java.util.List;
import java.util.UUID;

public record EvaluateInspectionCommand(
        InspectionId inspectionId,
        VehicleId vehicleId,
        List<InspectionResultSnapshot> results,
        UUID evaluatedBy) {

    public EvaluateInspectionCommand {
        if (inspectionId == null) throw new IllegalArgumentException("evaluation.error.inspection-id.required");
        if (vehicleId == null) throw new IllegalArgumentException("evaluation.error.vehicle-id.required");
        results = results == null ? List.of() : List.copyOf(results);
    }
}
