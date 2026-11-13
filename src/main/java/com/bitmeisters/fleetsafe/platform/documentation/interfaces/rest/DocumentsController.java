package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.documentation.application.queryservices.VehicleDocumentQueryService;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetExpiringDocumentsQuery;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetVehicleDocumentsQuery;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources.VehicleDocumentResource;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform.ResponseEntityFromVehicleDocumentQueryResultAssembler;
import com.bitmeisters.fleetsafe.platform.shared.application.internal.outboundservices.storage.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bitmeisters.fleetsafe.platform.documentation.application.commandservices.VehicleDocumentCommandService;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.AttachVehicleDocumentFileCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform.ResponseEntityFromVehicleDocumentCommandResultAssembler;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/documents", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vehicle Documents", description = "Documents of each vehicle and their validity")
public class DocumentsController {

    private final VehicleDocumentCommandService vehicleDocumentCommandService;
    private final VehicleDocumentQueryService vehicleDocumentQueryService;
    private final FileStorageService fileStorageService;
    private final MessageSource messageSource;

    public DocumentsController(VehicleDocumentCommandService vehicleDocumentCommandService,
                               VehicleDocumentQueryService vehicleDocumentQueryService,
                               FileStorageService fileStorageService, MessageSource messageSource) {
        this.vehicleDocumentCommandService = vehicleDocumentCommandService;
        this.vehicleDocumentQueryService = vehicleDocumentQueryService;
        this.fileStorageService = fileStorageService;
        this.messageSource = messageSource;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "List the documents of every vehicle",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Send expiresWithinDays to list only the documents that expire between today "
                    + "and that number of days, for example " + VehicleDocument.ALERT_DAYS + ". Ordered by expiration date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VehicleDocumentResource.class)))),
            @ApiResponse(responseCode = "400", description = "The number of days must be between 0 and 365"),
            @ApiResponse(responseCode = "403", description = "The role does not allow this operation")})
    public ResponseEntity<?> getVehicleDocuments(@RequestParam(required = false) Integer expiresWithinDays) {
        var documents = expiresWithinDays == null
                ? vehicleDocumentQueryService.handle(new GetVehicleDocumentsQuery())
                : vehicleDocumentQueryService.handle(new GetExpiringDocumentsQuery(expiresWithinDays));
        return ResponseEntityFromVehicleDocumentQueryResultAssembler.toResponseEntityFromList(documents, fileStorageService::temporaryReadUrl);
    }

    @PostMapping(value = "/{id}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Attach the scanned file of a document",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. A JPEG, PNG or PDF file that replaces the previous one.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File attached",
                    content = @Content(schema = @Schema(implementation = VehicleDocumentResource.class))),
            @ApiResponse(responseCode = "400", description = "The file is not a JPEG, PNG or PDF"),
            @ApiResponse(responseCode = "404", description = "There is no document with that identifier"),
            @ApiResponse(responseCode = "503", description = "The file could not be stored")})
    public ResponseEntity<?> attachVehicleDocumentFile(@PathVariable UUID id, @RequestPart("file") MultipartFile file) throws IOException {
        if (!FileStorageService.SUPPORTED_CONTENT_TYPES.contains(file.getContentType())) {
            return problem(HttpStatus.BAD_REQUEST, "documentation.document.error.file-type");
        }
        var fileUrl = fileStorageService.store(file.getContentType(), file.getBytes());
        if (fileUrl.isEmpty()) {
            return problem(HttpStatus.SERVICE_UNAVAILABLE, "documentation.document.error.file-unavailable");
        }
        var command = new AttachVehicleDocumentFileCommand(id, new FileUrl(fileUrl.get()));
        return ResponseEntityFromVehicleDocumentCommandResultAssembler.toResponseEntityFromResult(
                vehicleDocumentCommandService.handle(command), messageSource, HttpStatus.OK, fileStorageService::temporaryReadUrl);
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String messageKey) {
        var detail = messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }
}
