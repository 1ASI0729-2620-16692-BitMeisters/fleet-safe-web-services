package com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;

/**
 * Assignments of the vehicles of a company, newest first. When onlyActive is true, the assignments
 * that were already closed are left out.
 */
public record GetVehicleAssignmentsByCompanyIdQuery(CompanyId companyId, boolean onlyActive) {

    public GetVehicleAssignmentsByCompanyIdQuery {
        if (companyId == null) throw new IllegalArgumentException("fleet.error.company-id.required");
    }
}
