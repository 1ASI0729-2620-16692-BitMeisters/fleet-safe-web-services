package com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.UserId;

/**
 * The vehicle currently assigned to the driver behind a user account.
 */
public record GetActiveVehicleAssignmentOfUserQuery(CompanyId companyId, UserId userId) {

    public GetActiveVehicleAssignmentOfUserQuery {
        if (companyId == null) throw new IllegalArgumentException("fleet.error.company-id.required");
        if (userId == null) throw new IllegalArgumentException("fleet.driver.error.user-id.required");
    }
}
