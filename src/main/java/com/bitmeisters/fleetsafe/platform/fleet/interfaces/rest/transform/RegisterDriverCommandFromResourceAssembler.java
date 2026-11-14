package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.RegisterDriverCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.LicenseNumber;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.UserId;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.RegisterDriverResource;

import java.util.UUID;

public class RegisterDriverCommandFromResourceAssembler {

    public static RegisterDriverCommand toCommandFromResource(UUID companyId, RegisterDriverResource resource) {
        return new RegisterDriverCommand(
                FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(companyId),
                new UserId(resource.userId()),
                new LicenseNumber(resource.licenseNumber()),
                resource.licenseExpirationDate());
    }
}
