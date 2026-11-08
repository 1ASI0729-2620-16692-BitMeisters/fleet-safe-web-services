package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.documentation.application.commandservices.DocumentTypeCommandService;
import com.bitmeisters.fleetsafe.platform.documentation.application.queryservices.DocumentTypeQueryService;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetDocumentTypesQuery;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources.DocumentTypeResource;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources.RegisterDocumentTypeResource;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform.RegisterDocumentTypeCommandFromResourceAssembler;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform.ResponseEntityFromDocumentTypeCommandResultAssembler;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform.ResponseEntityFromDocumentTypeQueryResultAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/document-types", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Document Types", description = "Catalog of the documents a vehicle may need")
public class DocumentTypesController {

    private final DocumentTypeCommandService documentTypeCommandService;
    private final DocumentTypeQueryService documentTypeQueryService;
    private final MessageSource messageSource;

    public DocumentTypesController(DocumentTypeCommandService documentTypeCommandService,
                                   DocumentTypeQueryService documentTypeQueryService,
                                   MessageSource messageSource) {
        this.documentTypeCommandService = documentTypeCommandService;
        this.documentTypeQueryService = documentTypeQueryService;
        this.messageSource = messageSource;
    }

    @GetMapping
    @Operation(summary = "List the document types", description = "Roles: any authenticated user of the company.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document types",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DocumentTypeResource.class))))})
    public ResponseEntity<?> getDocumentTypes() {
        return ResponseEntityFromDocumentTypeQueryResultAssembler.toResponseEntityFromList(
                documentTypeQueryService.handle(new GetDocumentTypesQuery()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Register a document type", description = "Roles: ADMINISTRATOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Document type registered",
                    content = @Content(schema = @Schema(implementation = DocumentTypeResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "A document type with that code already exists")})
    public ResponseEntity<?> registerDocumentType(@Valid @RequestBody RegisterDocumentTypeResource resource) {
        var result = documentTypeCommandService.handle(RegisterDocumentTypeCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityFromDocumentTypeCommandResultAssembler.toResponseEntityFromResult(result, messageSource, HttpStatus.CREATED);
    }
}
