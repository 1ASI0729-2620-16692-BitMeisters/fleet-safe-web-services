package com.bitmeisters.fleetsafe.platform.fleet.domain.repositories;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Fleet;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.FleetId;

import java.util.List;

public interface FleetRepository {

    List<Fleet> findAllByCompanyId(CompanyId companyId);

    boolean existsByIdAndCompanyId(FleetId id, CompanyId companyId);

    Fleet save(Fleet fleet);
}
