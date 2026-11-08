package com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;

public record GetFleetsByCompanyIdQuery(CompanyId companyId) {

    public GetFleetsByCompanyIdQuery {
        if (companyId == null) throw new IllegalArgumentException("fleet.error.company-id.required");
    }
}
