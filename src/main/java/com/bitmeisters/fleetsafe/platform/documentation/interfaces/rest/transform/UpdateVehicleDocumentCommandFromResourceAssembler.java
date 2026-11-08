package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.UpdateVehicleDocumentCommand;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources.SaveVehicleDocumentResource;

import java.util.UUID;

public class UpdateVehicleDocumentCommandFromResourceAssembler {

    public static UpdateVehicleDocumentCommand toCommandFromResource(UUID vehicleId, UUID vehicleDocumentId,
                                                                     SaveVehicleDocumentResource resource) {
        return new UpdateVehicleDocumentCommand(
                vehicleDocumentId,
                DocumentationValueObjectFromStringAssembler.toVehicleIdFromUuid(vehicleId),
                DocumentationValueObjectFromStringAssembler.toDocumentTypeIdFromUuid(resource.documentTypeId()),
                DocumentationValueObjectFromStringAssembler.toDocumentNumberFromString(resource.number()),
                DocumentationValueObjectFromStringAssembler.toValidityPeriodFromDates(resource.issueDate(), resource.expirationDate()),
                DocumentationValueObjectFromStringAssembler.toFileUrlFromString(resource.fileUrl()));
    }
}
