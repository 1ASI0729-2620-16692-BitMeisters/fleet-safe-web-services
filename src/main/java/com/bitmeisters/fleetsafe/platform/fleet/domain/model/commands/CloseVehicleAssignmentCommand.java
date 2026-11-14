package com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleAssignmentId;

/**
 * Ends an active assignment today. The assignment stays as history.
 */
public record CloseVehicleAssignmentCommand(CompanyId companyId, VehicleAssignmentId assignmentId) {

    public CloseVehicleAssignmentCommand {
        if (companyId == null) throw new IllegalArgumentException("fleet.error.company-id.required");
        if (assignmentId == null) throw new IllegalArgumentException("fleet.assignment.error.id.required");
    }
}
