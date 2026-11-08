package com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.VehicleId;

import java.util.UUID;

public record AttachVehicleDocumentFileCommand(UUID vehicleDocumentId, VehicleId vehicleId, FileUrl fileUrl) {
    public AttachVehicleDocumentFileCommand {
        if (vehicleDocumentId == null) throw new IllegalArgumentException("documentation.document.error.id.required");
        if (vehicleId == null) throw new IllegalArgumentException("documentation.error.vehicle-id.required");
        if (fileUrl == null) throw new IllegalArgumentException("documentation.document.error.file-url.required");
    }
}
