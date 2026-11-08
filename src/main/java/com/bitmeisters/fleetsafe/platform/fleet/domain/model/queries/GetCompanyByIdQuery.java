package com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;

public record GetCompanyByIdQuery(CompanyId companyId) {

    public GetCompanyByIdQuery {
        if (companyId == null) throw new IllegalArgumentException("fleet.error.company-id.required");
    }
}
