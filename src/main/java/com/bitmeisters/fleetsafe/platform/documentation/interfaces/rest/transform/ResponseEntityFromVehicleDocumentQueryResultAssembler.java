package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.UnaryOperator;

public class ResponseEntityFromVehicleDocumentQueryResultAssembler {

    public static ResponseEntity<?> toResponseEntityFromList(List<VehicleDocument> vehicleDocuments, UnaryOperator<String> readUrlOf) {
        var vehicleDocumentResources = vehicleDocuments.stream()
                .map(vehicleDocument -> VehicleDocumentResourceFromEntityAssembler.toResourceFromEntity(vehicleDocument, readUrlOf))
                .toList();
        return ResponseEntity.ok(vehicleDocumentResources);
    }
}
