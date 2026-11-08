package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources.VehicleDocumentResource;

public class VehicleDocumentResourceFromEntityAssembler {

    public static VehicleDocumentResource toResourceFromEntity(VehicleDocument entity) {
        return new VehicleDocumentResource(
                entity.getId(),
                entity.getVehicleId().value(),
                entity.getDocumentTypeId().value(),
                entity.getNumber().value(),
                entity.getValidityPeriod().issueDate(),
                entity.getValidityPeriod().expirationDate(),
                entity.getStatus().name(),
                entity.getFileUrl() == null ? null : entity.getFileUrl().value(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
