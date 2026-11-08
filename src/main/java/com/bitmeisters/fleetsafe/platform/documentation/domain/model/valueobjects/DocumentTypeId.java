package com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects;

import java.util.UUID;

public record DocumentTypeId(UUID value) {

    public DocumentTypeId {
        if (value == null) {
            throw new IllegalArgumentException("documentation.document.error.document-type-id.required");
        }
    }
}
