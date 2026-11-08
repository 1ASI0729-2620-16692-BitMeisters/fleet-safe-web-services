package com.bitmeisters.fleetsafe.platform.fleet.application.queryservices;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Fleet;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetFleetsByCompanyIdQuery;

import java.util.List;

public interface FleetQueryService {

    List<Fleet> handle(GetFleetsByCompanyIdQuery query);
}
