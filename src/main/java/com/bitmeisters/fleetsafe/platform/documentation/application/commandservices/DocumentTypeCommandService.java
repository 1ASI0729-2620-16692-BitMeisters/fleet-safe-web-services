package com.bitmeisters.fleetsafe.platform.documentation.application.commandservices;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.DocumentType;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RegisterDocumentTypeCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

public interface DocumentTypeCommandService {

    Result<DocumentType, DocumentTypeCommandFailure> handle(RegisterDocumentTypeCommand command);
}
