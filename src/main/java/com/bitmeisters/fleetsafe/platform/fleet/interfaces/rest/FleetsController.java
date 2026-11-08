package com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.FleetCommandService;
import com.bitmeisters.fleetsafe.platform.fleet.application.queryservices.FleetQueryService;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetFleetsByCompanyIdQuery;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.CreateFleetResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.resources.FleetResource;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.CreateFleetCommandFromResourceAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.FleetValueObjectFromStringAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.ResponseEntityFromFleetCommandResultAssembler;
import com.bitmeisters.fleetsafe.platform.fleet.interfaces.rest.transform.ResponseEntityFromFleetQueryResultAssembler;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/fleets", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Fleet Management", description = "Companies, fleets, vehicles, drivers and their assignments")
public class FleetsController {

    private final FleetCommandService fleetCommandService;
    private final FleetQueryService fleetQueryService;
    private final MessageSource messageSource;

    public FleetsController(FleetCommandService fleetCommandService,
                            FleetQueryService fleetQueryService,
                            MessageSource messageSource) {
        this.fleetCommandService = fleetCommandService;
        this.fleetQueryService = fleetQueryService;
        this.messageSource = messageSource;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Create a fleet",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. The fleet belongs to the company of the signed-in user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Fleet created",
                    content = @Content(schema = @Schema(implementation = FleetResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "422", description = "The company has not been registered yet")})
    public ResponseEntity<?> createFleet(@AuthenticationPrincipal UserDetailsImpl principal,
                                         @Valid @RequestBody CreateFleetResource resource) {
        var command = CreateFleetCommandFromResourceAssembler.toCommandFromResource(principal.getCompanyId(), resource);
        return ResponseEntityFromFleetCommandResultAssembler.toResponseEntityFromResult(
                fleetCommandService.handle(command), messageSource, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List the fleets of the company", description = "Roles: any authenticated user of the company. Ordered by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fleets of the company",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = FleetResource.class))))})
    public ResponseEntity<?> getFleets(@AuthenticationPrincipal UserDetailsImpl principal) {
        var query = new GetFleetsByCompanyIdQuery(FleetValueObjectFromStringAssembler.toCompanyIdFromUuid(principal.getCompanyId()));
        return ResponseEntityFromFleetQueryResultAssembler.toResponseEntityFromList(fleetQueryService.handle(query));
    }
}
