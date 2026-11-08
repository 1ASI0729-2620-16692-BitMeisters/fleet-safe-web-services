package com.bitmeisters.fleetsafe.platform.fleet.domain.repositories;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Company;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.TaxId;

import java.util.Optional;

public interface CompanyRepository {

    Optional<Company> findById(CompanyId id);

    boolean existsById(CompanyId id);

    boolean existsByTaxId(TaxId taxId);

    Company save(Company company);
}
