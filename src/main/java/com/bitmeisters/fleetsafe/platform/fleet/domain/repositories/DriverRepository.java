package com.bitmeisters.fleetsafe.platform.fleet.domain.repositories;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Driver;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.UserId;

import java.util.List;
import java.util.Optional;

public interface DriverRepository {

    Optional<Driver> findByIdAndCompanyId(DriverId id, CompanyId companyId);

    Optional<Driver> findByUserIdAndCompanyId(UserId userId, CompanyId companyId);

    List<Driver> findAllByCompanyId(CompanyId companyId);

    boolean existsByUserId(UserId userId);

    Driver save(Driver driver);
}
