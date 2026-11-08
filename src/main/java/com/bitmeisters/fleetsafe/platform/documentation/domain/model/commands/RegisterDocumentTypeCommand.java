package com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeCode;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeName;

public record RegisterDocumentTypeCommand(
        DocumentTypeCode code,
        DocumentTypeName name,
        String description,
        boolean required) {

    public RegisterDocumentTypeCommand {
        if (code == null) throw new IllegalArgumentException("documentation.type.error.code.required");
        if (name == null) throw new IllegalArgumentException("documentation.type.error.name.required");
    }
}
