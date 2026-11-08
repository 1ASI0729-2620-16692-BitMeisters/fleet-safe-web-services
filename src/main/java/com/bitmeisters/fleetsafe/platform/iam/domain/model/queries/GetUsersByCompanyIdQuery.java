package com.bitmeisters.fleetsafe.platform.iam.domain.model.queries;

import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.iam.domain.model.valueobjects.Role;

public record GetUsersByCompanyIdQuery(CompanyId companyId, Role role, Boolean active) {

    public GetUsersByCompanyIdQuery(CompanyId companyId) {
        this(companyId, null, null);
    }

    public GetUsersByCompanyIdQuery {
        if (companyId == null) throw new IllegalArgumentException("iam.user.error.company-id.required");
    }
}
