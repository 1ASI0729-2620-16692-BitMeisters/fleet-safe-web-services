package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseEntityFromVehicleDocumentQueryResultAssembler {

    public static ResponseEntity<?> toResponseEntityFromList(List<VehicleDocument> vehicleDocuments) {
        var vehicleDocumentResources = vehicleDocuments.stream()
                .map(VehicleDocumentResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(vehicleDocumentResources);
    }
}
