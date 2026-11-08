package com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentNumber;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeId;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.ValidityPeriod;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.VehicleId;

import java.util.UUID;

public record UpdateVehicleDocumentCommand(
        UUID vehicleDocumentId,
        VehicleId vehicleId,
        DocumentTypeId documentTypeId,
        DocumentNumber number,
        ValidityPeriod validityPeriod,
        FileUrl fileUrl) {

    public UpdateVehicleDocumentCommand {
        if (vehicleDocumentId == null) throw new IllegalArgumentException("documentation.document.error.id.required");
        if (vehicleId == null) throw new IllegalArgumentException("documentation.error.vehicle-id.required");
        if (documentTypeId == null) throw new IllegalArgumentException("documentation.document.error.document-type-id.required");
        if (number == null) throw new IllegalArgumentException("documentation.document.error.number.required");
        if (validityPeriod == null) throw new IllegalArgumentException("documentation.document.error.issue-date.required");
    }
}
