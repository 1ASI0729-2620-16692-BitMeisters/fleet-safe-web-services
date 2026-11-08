package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.DocumentType;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources.DocumentTypeResource;

public class DocumentTypeResourceFromEntityAssembler {

    public static DocumentTypeResource toResourceFromEntity(DocumentType entity) {
        return new DocumentTypeResource(
                entity.getId(),
                entity.getCode().value(),
                entity.getName().value(),
                entity.getDescription(),
                entity.isRequired(),
                entity.isActive());
    }
}
