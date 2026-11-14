package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.VehicleAssignmentCommandService;
import com.bitmeisters.fleetsafe.platform.fleet.application.queryservices.VehicleAssignmentQueryService;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.CloseVehicleAssignmentCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetVehicleAssignmentsByCompanyIdQuery;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleAssignmentId;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.AssignVehicleResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.VehicleAssignmentResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.AssignVehicleToDriverCommandFromResourceAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.FleetValueObjectFromStringAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.ResponseEntityFromVehicleAssignmentCommandResultAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.ResponseEntityFromVehicleAssignmentQueryResultAssembler;
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
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/vehicle-assignments", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Fleet Management", description = "Companies, fleets, vehicles, drivers and their assignments")
public class VehicleAssignmentsController {

    private final VehicleAssignmentCommandService vehicleAssignmentCommandService;
    private final VehicleAssignmentQueryService vehicleAssignmentQueryService;
    private final MessageSource messageSource;

    public VehicleAssignmentsController(VehicleAssignmentCommandService vehicleAssignmentCommandService,
                                        VehicleAssignmentQueryService vehicleAssignmentQueryService,
                                        MessageSource messageSource) {
        this.vehicleAssignmentCommandService = vehicleAssignmentCommandService;
        this.vehicleAssignmentQueryService = vehicleAssignmentQueryService;
        this.messageSource = messageSource;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Assign a vehicle to a driver",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. The vehicle must be enabled, the license of the driver must be "
                    + "in force, and neither of them can have another active assignment. The assignment starts today.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Vehicle assigned",
                    content = @Content(schema = @Schema(implementation = VehicleAssignmentResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "The vehicle or the driver already has an active assignment"),
            @ApiResponse(responseCode = "422",
                    description = "The vehicle or the driver does not exist in your company, the vehicle is not enabled, or the license has expired")})
    public ResponseEntity<?> assignVehicle(@AuthenticationPrincipal UserDetailsImpl principal,
                                           @Valid @RequestBody AssignVehicleResource resource) {
        var command = AssignVehicleToDriverCommandFromResourceAssembler.toCommandFromResource(principal.getCompanyId(), resource);
        return ResponseEntityFromVehicleAssignmentCommandResultAssembler.toResponseEntityFromResult(
                vehicleAssignmentCommandService.handle(command), messageSource, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "List the vehicle assignments of the company",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Newest first, with the closed ones as history. Send active=true to see only the ones in force.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignments of the company",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = VehicleAssignmentResource.class))))})
    public ResponseEntity<?> getVehicleAssignments(
            @AuthenticationPrincipal UserDetailsImpl principal,
            @Parameter(description = "Only the assignments in force")
            @RequestParam(required = false, defaultValue = "false") boolean active) {
        var query = new GetVehicleAssignmentsByCompanyIdQuery(
                FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(principal.getCompanyId()), active);
        return ResponseEntityFromVehicleAssignmentQueryResultAssembler.toResponseEntityFromList(
                vehicleAssignmentQueryService.handle(query));
    }

    @PostMapping("/{assignmentId}/close")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Close a vehicle assignment",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Ends the assignment today and keeps it as history. "
                    + "The vehicle and the driver can then be assigned again.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assignment closed",
                    content = @Content(schema = @Schema(implementation = VehicleAssignmentResource.class))),
            @ApiResponse(responseCode = "404", description = "The company has no assignment with that identifier"),
            @ApiResponse(responseCode = "409", description = "The assignment is already closed")})
    public ResponseEntity<?> closeVehicleAssignment(@AuthenticationPrincipal UserDetailsImpl principal,
                                                    @PathVariable UUID assignmentId) {
        var command = new CloseVehicleAssignmentCommand(
                FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(principal.getCompanyId()),
                new VehicleAssignmentId(assignmentId));
        return ResponseEntityFromVehicleAssignmentCommandResultAssembler.toResponseEntityFromResult(
                vehicleAssignmentCommandService.handle(command), messageSource, HttpStatus.OK);
    }
}
