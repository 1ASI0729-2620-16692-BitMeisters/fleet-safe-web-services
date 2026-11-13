package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices.InspectionItemCommandService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.queryservices.InspectionItemQueryService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.DeactivateInspectionItemCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionItemByIdQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionItemsQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ItemSystem;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.CreateInspectionItemResource;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.DeactivateInspectionItemResource;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.InspectionItemResource;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.UpdateInspectionItemResource;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform.CreateInspectionItemCommandFromResourceAssembler;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform.ResponseEntityFromInspectionItemCommandResultAssembler;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform.ResponseEntityFromInspectionItemQueryResultAssembler;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform.UpdateInspectionItemCommandFromResourceAssembler;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST Controller exposing checklist inspection item catalog management endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/inspection-items", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Inspection Items", description = "Catalog of components, safety items, and documents to inspect on vehicles")
public class InspectionItemsController {

    private final InspectionItemCommandService inspectionItemCommandService;
    private final InspectionItemQueryService inspectionItemQueryService;
    private final MessageSource messageSource;

    /**
     * Constructs the controller with required command/query services and message source.
     *
     * @param inspectionItemCommandService the command service
     * @param inspectionItemQueryService   the query service
     * @param messageSource                the internationalization message source
     */
    public InspectionItemsController(InspectionItemCommandService inspectionItemCommandService,
                                    InspectionItemQueryService inspectionItemQueryService,
                                    MessageSource messageSource) {
        this.inspectionItemCommandService = inspectionItemCommandService;
        this.inspectionItemQueryService = inspectionItemQueryService;
        this.messageSource = messageSource;
    }

    /**
     * Creates a new inspection item in the catalog.
     *
     * @param resource the creation payload
     * @return 201 Created on success, or ProblemDetail on error
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Create an inspection item", description = "Roles: ADMINISTRATOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Inspection item created",
                    content = @Content(schema = @Schema(implementation = InspectionItemResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "403", description = "The role does not allow this operation"),
            @ApiResponse(responseCode = "409", description = "An inspection item with that code already exists")})
    public ResponseEntity<?> createInspectionItem(@Valid @RequestBody CreateInspectionItemResource resource) {
        var command = CreateInspectionItemCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = inspectionItemCommandService.handle(command);
        return ResponseEntityFromInspectionItemCommandResultAssembler.toResponseEntityFromResult(result, messageSource, HttpStatus.CREATED);
    }

    /**
     * Lists inspection items matching optional vehicle system and active status filters.
     *
     * @param system optional system filter
     * @param active optional active status filter
     * @return 200 OK with list of inspection items
     */
    @GetMapping
    @Operation(summary = "List inspection items", description = "Roles: any authenticated user. Filter by vehicle system and active status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of inspection items",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = InspectionItemResource.class))))})
    public ResponseEntity<?> getInspectionItems(
            @RequestParam(required = false) ItemSystem system,
            @RequestParam(required = false) Boolean active) {
        var query = new GetInspectionItemsQuery(system, active);
        var items = inspectionItemQueryService.handle(query);
        return ResponseEntityFromInspectionItemQueryResultAssembler.toResponseEntityFromList(items);
    }

    /**
     * Retrieves an inspection item by its persistent unique identifier.
     *
     * @param id the item UUID
     * @return 200 OK if found, or 404 Not Found ProblemDetail
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get an inspection item by ID", description = "Roles: any authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inspection item found",
                    content = @Content(schema = @Schema(implementation = InspectionItemResource.class))),
            @ApiResponse(responseCode = "404", description = "Inspection item not found")})
    public ResponseEntity<?> getInspectionItemById(@PathVariable UUID id) {
        var query = new GetInspectionItemByIdQuery(new InspectionItemId(id));
        return inspectionItemQueryService.handle(query)
                .<ResponseEntity<?>>map(ResponseEntityFromInspectionItemQueryResultAssembler::toResponseEntityFromInspectionItem)
                .orElseGet(() -> ResponseEntityFromInspectionItemQueryResultAssembler.notFound(messageSource, "inspection.item.error.not-found"));
    }

    /**
     * Updates an existing inspection item in the catalog.
     *
     * @param id       the item UUID
     * @param resource the update payload
     * @return 200 OK on success, or ProblemDetail on error
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Update an inspection item", description = "Roles: ADMINISTRATOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inspection item updated",
                    content = @Content(schema = @Schema(implementation = InspectionItemResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "403", description = "The role does not allow this operation"),
            @ApiResponse(responseCode = "404", description = "Inspection item not found")})
    public ResponseEntity<?> updateInspectionItem(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateInspectionItemResource resource) {
        var command = UpdateInspectionItemCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = inspectionItemCommandService.handle(command);
        return ResponseEntityFromInspectionItemCommandResultAssembler.toResponseEntityFromResult(result, messageSource, HttpStatus.OK);
    }

    /**
     * Deactivates an inspection item from the active checklist catalog without deleting it.
     *
     * @param id the item UUID
     * @return 200 OK on success, or ProblemDetail on error
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Deactivate an inspection item", description = "Roles: ADMINISTRATOR. Send isActive false; deactivated items are excluded from active checklists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inspection item deactivated",
                    content = @Content(schema = @Schema(implementation = InspectionItemResource.class))),
            @ApiResponse(responseCode = "403", description = "The role does not allow this operation"),
            @ApiResponse(responseCode = "404", description = "Inspection item not found"),
            @ApiResponse(responseCode = "409", description = "The inspection item is already inactive")})
    public ResponseEntity<?> deactivateInspectionItem(@PathVariable UUID id, @Valid @RequestBody DeactivateInspectionItemResource resource) {
        var command = new DeactivateInspectionItemCommand(new InspectionItemId(id));
        var result = inspectionItemCommandService.handle(command);
        return ResponseEntityFromInspectionItemCommandResultAssembler.toResponseEntityFromResult(result, messageSource, HttpStatus.OK);
    }
}
