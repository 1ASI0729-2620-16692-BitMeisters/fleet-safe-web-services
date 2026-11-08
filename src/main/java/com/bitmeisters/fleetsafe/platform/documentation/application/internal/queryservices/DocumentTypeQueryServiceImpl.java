package com.bitmeisters.fleetsafe.platform.documentation.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.documentation.application.queryservices.DocumentTypeQueryService;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.DocumentType;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetDocumentTypesQuery;
import com.bitmeisters.fleetsafe.platform.documentation.domain.repositories.DocumentTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DocumentTypeQueryServiceImpl implements DocumentTypeQueryService {

    private final DocumentTypeRepository documentTypeRepository;

    public DocumentTypeQueryServiceImpl(DocumentTypeRepository documentTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
    }

    @Override
    public List<DocumentType> handle(GetDocumentTypesQuery query) {
        var results = documentTypeRepository.findAll();
        log.debug("Found {} document types", results.size());
        return results;
    }
}
