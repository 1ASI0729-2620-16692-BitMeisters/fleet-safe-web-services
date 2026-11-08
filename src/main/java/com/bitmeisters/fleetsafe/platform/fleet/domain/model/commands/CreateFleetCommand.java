package com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.FleetName;

public record CreateFleetCommand(
        CompanyId companyId,
        FleetName name,
        String description) {

    public static final int DESCRIPTION_MAX_LENGTH = 500;

    public CreateFleetCommand {
        if (companyId == null) throw new IllegalArgumentException("fleet.error.company-id.required");
        if (name == null) throw new IllegalArgumentException("fleet.error.name.required");
        if (description == null || description.isBlank()) {
            description = null;
        } else {
            description = description.strip();
            if (description.length() > DESCRIPTION_MAX_LENGTH) {
                throw new IllegalArgumentException("fleet.error.description.size");
            }
        }
    }
}
