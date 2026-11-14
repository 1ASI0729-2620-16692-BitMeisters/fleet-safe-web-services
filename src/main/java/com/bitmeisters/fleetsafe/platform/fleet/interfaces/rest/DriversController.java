package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.DriverCommandService;
import com.bitmeisters.fleetsafe.platform.fleet.application.queryservices.DriverQueryService;
import com.bitmeisters.fleetsafe.platform.fleet.application.queryservices.VehicleAssignmentQueryService;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetActiveVehicleAssignmentOfUserQuery;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetDriversByCompanyIdQuery;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.UserId;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.DriverResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.DriverVehicleAssignmentResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.RegisterDriverResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.*;
import com.bitmeisters.fleetsafe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/drivers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Fleet Management", description = "Companies, fleets, vehicles, drivers and their assignments")
public class DriversController {

    private final DriverCommandService driverCommandService;
    private final DriverQueryService driverQueryService;
    private final VehicleAssignmentQueryService vehicleAssignmentQueryService;
    private final MessageSource messageSource;

    public DriversController(DriverCommandService driverCommandService,
                             DriverQueryService driverQueryService,
                             VehicleAssignmentQueryService vehicleAssignmentQueryService,
                             MessageSource messageSource) {
        this.driverCommandService = driverCommandService;
        this.driverQueryService = driverQueryService;
        this.vehicleAssignmentQueryService = vehicleAssignmentQueryService;
        this.messageSource = messageSource;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Register a driver",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Registers the license of the driver of an existing user account. "
                    + "A user account can be registered as a driver only once.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Driver registered",
                    content = @Content(schema = @Schema(implementation = DriverResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "The user account is already registered as a driver"),
            @ApiResponse(responseCode = "422", description = "The user account does not exist")})
    public ResponseEntity<?> registerDriver(@AuthenticationPrincipal UserDetailsImpl principal,
                                            @Valid @RequestBody RegisterDriverResource resource) {
        var command = RegisterDriverCommandFromResourceAssembler.toCommandFromResource(principal.getCompanyId(), resource);
        return ResponseEntityFromDriverCommandResultAssembler.toResponseEntityFromResult(
                driverCommandService.handle(command), messageSource, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "List the drivers of the company",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Ordered by registration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Drivers of the company",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DriverResource.class))))})
    public ResponseEntity<?> getDrivers(@AuthenticationPrincipal UserDetailsImpl principal) {
        var query = new GetDriversByCompanyIdQuery(
                FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(principal.getCompanyId()));
        return ResponseEntityFromDriverQueryResultAssembler.toResponseEntityFromList(driverQueryService.handle(query));
    }

    @GetMapping("/me/vehicle-assignment")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Get the active vehicle assignment of the signed-in driver",
            description = "Roles: DRIVER. The driver comes from the token. A driver has at most one active assignment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The active assignment, with the assigned vehicle",
                    content = @Content(schema = @Schema(implementation = DriverVehicleAssignmentResource.class))),
            @ApiResponse(responseCode = "404", description = "The driver has no vehicle assigned")})
    public ResponseEntity<?> getMyVehicleAssignment(@AuthenticationPrincipal UserDetailsImpl principal) {
        var query = new GetActiveVehicleAssignmentOfUserQuery(
                FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(principal.getCompanyId()),
                new UserId(principal.getId()));
        return ResponseEntityFromVehicleAssignmentQueryResultAssembler.toResponseEntityFromOptional(
                vehicleAssignmentQueryService.handle(query), messageSource);
    }
}
