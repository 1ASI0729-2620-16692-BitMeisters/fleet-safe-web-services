package com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.FileUrl;

import java.util.UUID;

public record AttachVehicleDocumentFileCommand(UUID vehicleDocumentId, FileUrl fileUrl) {
    public AttachVehicleDocumentFileCommand {
        if (vehicleDocumentId == null) throw new IllegalArgumentException("documentation.document.error.id.required");
        if (fileUrl == null) throw new IllegalArgumentException("documentation.document.error.file-url.required");
    }
}
