package com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.documentation.application.queryservices.VehicleDocumentQueryService;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetExpiringDocumentsQuery;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.queries.GetVehicleDocumentsQuery;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.resources.VehicleDocumentResource;
import com.bitmeisters.fleetsafe.platform.documentation.interfaces.rest.transform.ResponseEntityFromVehicleDocumentQueryResultAssembler;
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

@RestController
@RequestMapping(value = "/api/v1/documents", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vehicle Documents", description = "Documents of each vehicle and their validity")
public class DocumentsController {

    private final VehicleDocumentQueryService vehicleDocumentQueryService;

    public DocumentsController(VehicleDocumentQueryService vehicleDocumentQueryService) {
        this.vehicleDocumentQueryService = vehicleDocumentQueryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "List the documents of every vehicle",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Ordered by expiration date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VehicleDocumentResource.class)))),
            @ApiResponse(responseCode = "403", description = "The role does not allow this operation")})
    public ResponseEntity<?> getVehicleDocuments() {
        return ResponseEntityFromVehicleDocumentQueryResultAssembler.toResponseEntityFromList(
                vehicleDocumentQueryService.handle(new GetVehicleDocumentsQuery()));
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "List the documents about to expire",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Documents that expire between today and the given number of days, by default "
                    + VehicleDocument.ALERT_DAYS + ". Ordered by expiration date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Documents about to expire",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VehicleDocumentResource.class)))),
            @ApiResponse(responseCode = "400", description = "The number of days must be between 0 and 365"),
            @ApiResponse(responseCode = "403", description = "The role does not allow this operation")})
    public ResponseEntity<?> getExpiringDocuments(@RequestParam(defaultValue = "" + VehicleDocument.ALERT_DAYS) int days) {
        return ResponseEntityFromVehicleDocumentQueryResultAssembler.toResponseEntityFromList(
                vehicleDocumentQueryService.handle(new GetExpiringDocumentsQuery(days)));
    }
}
