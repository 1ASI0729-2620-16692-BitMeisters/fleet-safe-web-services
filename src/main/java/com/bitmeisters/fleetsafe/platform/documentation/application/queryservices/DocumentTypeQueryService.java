package com.bitmeisters.fleetsafe.platform.documentation.application.queryservices;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.DocumentType;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetDocumentTypesQuery;

import java.util.List;

public interface DocumentTypeQueryService {

    List<DocumentType> handle(GetDocumentTypesQuery query);
}
