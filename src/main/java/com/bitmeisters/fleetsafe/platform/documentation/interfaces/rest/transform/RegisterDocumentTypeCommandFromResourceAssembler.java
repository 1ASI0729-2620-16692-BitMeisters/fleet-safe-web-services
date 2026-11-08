package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RegisterDocumentTypeCommand;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources.RegisterDocumentTypeResource;

public class RegisterDocumentTypeCommandFromResourceAssembler {

    public static RegisterDocumentTypeCommand toCommandFromResource(RegisterDocumentTypeResource resource) {
        return new RegisterDocumentTypeCommand(
                DocumentationValueObjectFromStringAssembler.toDocumentTypeCodeFromString(resource.code()),
                DocumentationValueObjectFromStringAssembler.toDocumentTypeNameFromString(resource.name()),
                resource.description(),
                resource.isRequired());
    }
}
