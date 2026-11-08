package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Company;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.CompanyResource;

public class CompanyResourceFromEntityAssembler {

    public static CompanyResource toResourceFromEntity(Company entity) {
        return new CompanyResource(
                entity.getId(),
                entity.getName().value(),
                entity.getTaxId().value(),
                entity.getAddress(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getCreatedAt());
    }
}
