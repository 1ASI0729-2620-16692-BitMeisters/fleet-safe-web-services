package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.VehicleCommandService;
import com.bitmeisters.fleetsafe.platform.fleet.application.queryservices.VehicleQueryService;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetVehicleByIdQuery;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetVehiclesByCompanyIdQuery;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.RegisterVehicleResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.UpdateVehicleResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.VehicleResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.FleetValueObjectFromStringAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.RegisterVehicleCommandFromResourceAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.ResponseEntityFromVehicleCommandResultAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.ResponseEntityFromVehicleQueryResultAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.UpdateVehicleCommandFromResourceAssembler;
import com.bitmeisters.fleetsafe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/vehicles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Fleet Management", description = "Companies, fleets, vehicles, drivers and their assignments")
public class VehiclesController {

    private final VehicleCommandService vehicleCommandService;
    private final VehicleQueryService vehicleQueryService;
    private final MessageSource messageSource;

    public VehiclesController(VehicleCommandService vehicleCommandService,
                              VehicleQueryService vehicleQueryService,
                              MessageSource messageSource) {
        this.vehicleCommandService = vehicleCommandService;
        this.vehicleQueryService = vehicleQueryService;
        this.messageSource = messageSource;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Register a vehicle",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Adds a vehicle to a fleet of your company. The vehicle starts enabled.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehicle registered",
                    content = @Content(schema = @Schema(implementation = VehicleResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "The plate is already registered"),
            @ApiResponse(responseCode = "422", description = "The fleet does not exist in your company")})
    public ResponseEntity<?> registerVehicle(@AuthenticationPrincipal UserDetailsImpl principal,
                                             @Valid @RequestBody RegisterVehicleResource resource) {
        var command = RegisterVehicleCommandFromResourceAssembler.toCommandFromResource(principal.getCompanyId(), resource);
        return ResponseEntityFromVehicleCommandResultAssembler.toResponseEntityFromResult(
                vehicleCommandService.handle(command), messageSource, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List the vehicles of the company",
            description = "Roles: any authenticated user of the company. Ordered by plate. Send status to see only the vehicles in that status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicles of the company",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VehicleResource.class)))),
            @ApiResponse(responseCode = "400", description = "The status is not ENABLED, OBSERVED or NOT_ENABLED")})
    public ResponseEntity<?> getVehicles(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Parameter(description = "Only the vehicles in this status", schema = @Schema(allowableValues = {"ENABLED", "OBSERVED", "NOT_ENABLED"}))
            @RequestParam(required = false) String status) {
        var query = new GetVehiclesByCompanyIdQuery(
                FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(principal.getCompanyId()),
                FleetValueObjectFromStringAssembler.toVehicleStatusFilterFromString(status));
        return ResponseEntityFromVehicleQueryResultAssembler.toResponseEntityFromList(vehicleQueryService.handle(query));
    }

    @GetMapping("/{vehicleId}")
    @Operation(summary = "Get a vehicle", description = "Roles: any authenticated user of the company.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The vehicle",
                    content = @Content(schema = @Schema(implementation = VehicleResource.class))),
            @ApiResponse(responseCode = "404", description = "The company has no vehicle with that identifier")})
    public ResponseEntity<?> getVehicleById(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable UUID vehicleId) {
        var query = new GetVehicleByIdQuery(
                FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(principal.getCompanyId()),
                FleetValueObjectFromStringAssembler.toVehicleIdFromUuid(vehicleId));
        return ResponseEntityFromVehicleQueryResultAssembler.toResponseEntityFromOptional(
                vehicleQueryService.handle(query), messageSource);
    }

    @PutMapping(value = "/{vehicleId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Update a vehicle",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Changes brand, model, year, type and capacity. "
                    + "The plate, the fleet and the status are not editable.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehicle updated",
                    content = @Content(schema = @Schema(implementation = VehicleResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "The company has no vehicle with that identifier")})
    public ResponseEntity<?> updateVehicle(@AuthenticationPrincipal UserDetailsImpl principal, @PathVariable UUID vehicleId,
                                           @Valid @RequestBody UpdateVehicleResource resource) {
        var command = UpdateVehicleCommandFromResourceAssembler.toCommandFromResource(principal.getCompanyId(), vehicleId, resource);
        return ResponseEntityFromVehicleCommandResultAssembler.toResponseEntityFromResult(
                vehicleCommandService.handle(command), messageSource, HttpStatus.OK);
    }
}
