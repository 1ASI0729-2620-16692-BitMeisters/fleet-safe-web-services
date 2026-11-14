package com.bitmeisters.fleetsafe.platform.fleet.application.queryservices;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Driver;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetDriversByCompanyIdQuery;

import java.util.List;

public interface DriverQueryService {

    List<Driver> handle(GetDriversByCompanyIdQuery query);
}
