package com.bitmeisters.fleetsafe.platform.fleet.application.queryservices;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.VehicleAssignment;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetActiveVehicleAssignmentOfUserQuery;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetVehicleAssignmentsByCompanyIdQuery;

import java.util.List;
import java.util.Optional;

public interface VehicleAssignmentQueryService {

    List<VehicleAssignment> handle(GetVehicleAssignmentsByCompanyIdQuery query);

    Optional<ActiveVehicleAssignment> handle(GetActiveVehicleAssignmentOfUserQuery query);
}
