package com.bitmeisters.fleetsafe.platform.fleet.application.queryservices;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Company;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetCompanyByIdQuery;

import java.util.Optional;

public interface CompanyQueryService {

    Optional<Company> handle(GetCompanyByIdQuery query);
}
