package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.DocumentType;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class ResponseEntityFromDocumentTypeQueryResultAssembler {

    public static ResponseEntity<?> toResponseEntityFromList(List<DocumentType> documentTypes) {
        var documentTypeResources = documentTypes.stream()
                .map(DocumentTypeResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(documentTypeResources);
    }
}
