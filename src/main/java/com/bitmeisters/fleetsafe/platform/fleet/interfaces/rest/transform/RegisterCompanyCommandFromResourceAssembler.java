package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.RegisterCompanyCommand;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.RegisterCompanyResource;

import java.util.UUID;

public class RegisterCompanyCommandFromResourceAssembler {

    public static RegisterCompanyCommand toCommandFromResource(UUID companyId, RegisterCompanyResource resource) {
        return new RegisterCompanyCommand(
                FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(companyId),
                FleetValueObjectFromStringAssembler.toCompanyNameFromString(resource.name()),
                FleetValueObjectFromStringAssembler.toTaxIdFromString(resource.taxId()),
                resource.address(),
                resource.phone(),
                resource.email());
    }
}
