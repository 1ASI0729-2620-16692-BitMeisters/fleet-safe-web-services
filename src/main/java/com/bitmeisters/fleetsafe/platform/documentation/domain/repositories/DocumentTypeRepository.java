package com.bitmeisters.fleetsafe.platform.documentation.domain.repositories;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.DocumentType;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeCode;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.DocumentTypeId;

import java.util.List;
import java.util.Optional;

public interface DocumentTypeRepository {

    Optional<DocumentType> findById(DocumentTypeId id);

    List<DocumentType> findAll();

    boolean existsById(DocumentTypeId id);

    boolean existsByCode(DocumentTypeCode code);

    DocumentType save(DocumentType documentType);
}
