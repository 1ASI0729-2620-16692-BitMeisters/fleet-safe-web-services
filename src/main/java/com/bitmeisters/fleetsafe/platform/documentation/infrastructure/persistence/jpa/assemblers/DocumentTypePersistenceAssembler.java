package com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.DocumentType;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeCode;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeName;
import com.bitmeisters.fleetsafe.platform.documentation.infrastructure.persistence.jpa.entities.DocumentTypePersistenceEntity;

public final class DocumentTypePersistenceAssembler {

    private DocumentTypePersistenceAssembler() {
    }

    public static DocumentType toDomainFromPersistence(DocumentTypePersistenceEntity entity) {
        if (entity == null) return null;
        return DocumentType.restore(
                entity.getId(),
                new DocumentTypeCode(entity.getCode()),
                new DocumentTypeName(entity.getName()),
                entity.getDescription(),
                entity.isRequired(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    public static DocumentTypePersistenceEntity toPersistenceFromDomain(DocumentType documentType) {
        if (documentType == null) return null;
        var entity = new DocumentTypePersistenceEntity();
        if (documentType.getId() != null) {
            entity.setId(documentType.getId());
            entity.setCreatedAt(documentType.getCreatedAt());
        }
        entity.setCode(documentType.getCode().value());
        entity.setName(documentType.getName().value());
        entity.setDescription(documentType.getDescription());
        entity.setRequired(documentType.isRequired());
        entity.setActive(documentType.isActive());
        return entity;
    }
}
