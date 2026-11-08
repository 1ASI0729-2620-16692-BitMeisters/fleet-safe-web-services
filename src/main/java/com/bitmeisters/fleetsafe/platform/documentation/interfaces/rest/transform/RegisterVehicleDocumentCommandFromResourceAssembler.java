package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RegisterVehicleDocumentCommand;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources.SaveVehicleDocumentResource;

import java.util.UUID;

public class RegisterVehicleDocumentCommandFromResourceAssembler {

    public static RegisterVehicleDocumentCommand toCommandFromResource(UUID vehicleId, SaveVehicleDocumentResource resource) {
        return new RegisterVehicleDocumentCommand(
                DocumentationValueObjectFromStringAssembler.toVehicleIdFromUuid(vehicleId),
                DocumentationValueObjectFromStringAssembler.toDocumentTypeIdFromUuid(resource.documentTypeId()),
                DocumentationValueObjectFromStringAssembler.toDocumentNumberFromString(resource.number()),
                DocumentationValueObjectFromStringAssembler.toValidityPeriodFromDates(resource.issueDate(), resource.expirationDate()),
                DocumentationValueObjectFromStringAssembler.toFileUrlFromString(resource.fileUrl()));
    }
}
