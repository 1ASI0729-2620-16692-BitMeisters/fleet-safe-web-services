package com.bitmeisters.fleetsafe.platform.documentation.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.documentation.application.commandservices.DocumentTypeCommandFailure;
import com.bitmeisters.fleetsafe.platform.documentation.application.commandservices.DocumentTypeCommandService;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.DocumentType;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RegisterDocumentTypeCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.repositories.DocumentTypeRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class DocumentTypeCommandServiceImpl implements DocumentTypeCommandService {

    private final DocumentTypeRepository documentTypeRepository;

    public DocumentTypeCommandServiceImpl(DocumentTypeRepository documentTypeRepository) {
        this.documentTypeRepository = documentTypeRepository;
    }

    @Override
    @Transactional
    public Result<DocumentType, DocumentTypeCommandFailure> handle(RegisterDocumentTypeCommand command) {
        if (documentTypeRepository.existsByCode(command.code())) {
            log.warn("Duplicate document type code: code={}", command.code().value());
            return Result.failure(new DocumentTypeCommandFailure.DuplicateCode());
        }
        var documentType = documentTypeRepository.save(new DocumentType(command));
        log.info("Document type registered: id={}, code={}, required={}",
                documentType.getId(), command.code().value(), command.required());
        return Result.success(documentType);
    }
}
