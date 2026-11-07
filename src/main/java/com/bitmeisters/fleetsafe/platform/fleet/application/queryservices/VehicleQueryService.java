package com.bitmeisters.fleetsafe.platform.fleet.application.queryservices;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Vehicle;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetVehicleByIdQuery;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetVehiclesByCompanyIdQuery;

import java.util.List;
import java.util.Optional;

public interface VehicleQueryService {

    List<Vehicle> handle(GetVehiclesByCompanyIdQuery query);

    Optional<Vehicle> handle(GetVehicleByIdQuery query);
}
