package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.documentation.application.commandservices.VehicleDocumentCommandService;
import com.bitmeisters.fleetsafe.platform.documentation.application.queryservices.VehicleDocumentQueryService;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.AttachVehicleDocumentFileCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetDocumentsByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.FileUrl;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources.SaveVehicleDocumentResource;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources.VehicleDocumentResource;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform.RegisterVehicleDocumentCommandFromResourceAssembler;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform.ResponseEntityFromVehicleDocumentCommandResultAssembler;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform.ResponseEntityFromVehicleDocumentQueryResultAssembler;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform.UpdateVehicleDocumentCommandFromResourceAssembler;
import com.bitmeisters.fleetsafe.platform.shared.application.internal.outboundservices.storage.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/vehicles/{vehicleId}/documents", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vehicle Documents", description = "Documents of each vehicle and their validity")
public class VehicleDocumentsController {

    private final VehicleDocumentCommandService vehicleDocumentCommandService;
    private final VehicleDocumentQueryService vehicleDocumentQueryService;
    private final FileStorageService fileStorageService;
    private final MessageSource messageSource;

    public VehicleDocumentsController(VehicleDocumentCommandService vehicleDocumentCommandService,
                                      VehicleDocumentQueryService vehicleDocumentQueryService,
                                      FileStorageService fileStorageService,
                                      MessageSource messageSource) {
        this.vehicleDocumentCommandService = vehicleDocumentCommandService;
        this.vehicleDocumentQueryService = vehicleDocumentQueryService;
        this.fileStorageService = fileStorageService;
        this.messageSource = messageSource;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Register a document of a vehicle",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. The status is derived from the expiration date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Document registered",
                    content = @Content(schema = @Schema(implementation = VehicleDocumentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "422", description = "The document type does not exist")})
    public ResponseEntity<?> registerVehicleDocument(@PathVariable UUID vehicleId, @Valid @RequestBody SaveVehicleDocumentResource resource) {
        var command = RegisterVehicleDocumentCommandFromResourceAssembler.toCommandFromResource(vehicleId, resource);
        return ResponseEntityFromVehicleDocumentCommandResultAssembler.toResponseEntityFromResult(
                vehicleDocumentCommandService.handle(command), messageSource, HttpStatus.CREATED, fileStorageService::temporaryReadUrl);
    }

    @GetMapping
    @Operation(summary = "List the documents of a vehicle",
            description = "Roles: any authenticated user of the company. Ordered by expiration date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents of the vehicle",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VehicleDocumentResource.class))))})
    public ResponseEntity<?> getDocumentsByVehicle(@PathVariable UUID vehicleId) {
        return ResponseEntityFromVehicleDocumentQueryResultAssembler.toResponseEntityFromList(
                vehicleDocumentQueryService.handle(new GetDocumentsByVehicleIdQuery(new VehicleId(vehicleId))),
                fileStorageService::temporaryReadUrl);
    }

    @PutMapping("/{documentId}")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Update a document of a vehicle",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. The status is recalculated from the new expiration date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document updated",
                    content = @Content(schema = @Schema(implementation = VehicleDocumentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "The vehicle has no document with that identifier"),
            @ApiResponse(responseCode = "422", description = "The document type does not exist")})
    public ResponseEntity<?> updateVehicleDocument(@PathVariable UUID vehicleId, @PathVariable UUID documentId,
                                                   @Valid @RequestBody SaveVehicleDocumentResource resource) {
        var command = UpdateVehicleDocumentCommandFromResourceAssembler.toCommandFromResource(vehicleId, documentId, resource);
        return ResponseEntityFromVehicleDocumentCommandResultAssembler.toResponseEntityFromResult(
                vehicleDocumentCommandService.handle(command), messageSource, HttpStatus.OK, fileStorageService::temporaryReadUrl);
    }

    @PostMapping(value = "/{documentId}/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Attach the scanned file of a document",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. A JPEG, PNG or PDF file that replaces the previous one.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File attached",
                    content = @Content(schema = @Schema(implementation = VehicleDocumentResource.class))),
            @ApiResponse(responseCode = "400", description = "The file is not a JPEG, PNG or PDF"),
            @ApiResponse(responseCode = "404", description = "The vehicle has no document with that identifier"),
            @ApiResponse(responseCode = "503", description = "The file could not be stored")})
    public ResponseEntity<?> attachVehicleDocumentFile(@PathVariable UUID vehicleId, @PathVariable UUID documentId,
                                                       @RequestPart("file") MultipartFile file) throws IOException {
        if (!FileStorageService.SUPPORTED_CONTENT_TYPES.contains(file.getContentType())) {
            return problem(HttpStatus.BAD_REQUEST, "documentation.document.error.file-type");
        }
        var fileUrl = fileStorageService.store(file.getContentType(), file.getBytes());
        if (fileUrl.isEmpty()) {
            return problem(HttpStatus.SERVICE_UNAVAILABLE, "documentation.document.error.file-unavailable");
        }
        var command = new AttachVehicleDocumentFileCommand(documentId, new VehicleId(vehicleId), new FileUrl(fileUrl.get()));
        return ResponseEntityFromVehicleDocumentCommandResultAssembler.toResponseEntityFromResult(
                vehicleDocumentCommandService.handle(command), messageSource, HttpStatus.OK, fileStorageService::temporaryReadUrl);
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String messageKey) {
        var detail = messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }
}
