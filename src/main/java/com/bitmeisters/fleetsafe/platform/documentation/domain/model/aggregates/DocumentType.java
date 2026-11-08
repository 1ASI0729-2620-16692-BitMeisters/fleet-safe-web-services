package com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RegisterDocumentTypeCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeCode;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeName;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class DocumentType extends AbstractDomainAggregateRoot<DocumentType> {

    private UUID id;
    private DocumentTypeCode code;
    private DocumentTypeName name;
    private String description;
    private boolean required;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    public DocumentType(RegisterDocumentTypeCommand command) {
        this.code = command.code();
        this.name = command.name();
        this.description = command.description();
        this.required = command.required();
        this.active = true;
    }

    public static DocumentType restore(UUID id, DocumentTypeCode code, DocumentTypeName name, String description,
                                       boolean required, boolean active, Instant createdAt, Instant updatedAt) {
        var documentType = new DocumentType(new RegisterDocumentTypeCommand(code, name, description, required));
        documentType.id = id;
        documentType.active = active;
        documentType.createdAt = createdAt;
        documentType.updatedAt = updatedAt;
        return documentType;
    }

    public boolean isMandatoryForOperation() {
        return required && active;
    }
}
