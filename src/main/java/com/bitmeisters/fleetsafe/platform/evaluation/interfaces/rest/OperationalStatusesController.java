package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices.OperationalAuthorizationQueryService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetCurrentAuthorizationByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetCurrentAuthorizationsQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.AuthorizationStatus;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources.OperationalAuthorizationResource;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform.ResponseEntityFromOperationalAuthorizationQueryResultAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/operational-statuses", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Operational Statuses", description = "Whether each vehicle may operate today, according to its latest authorization")
public class OperationalStatusesController {

    private final OperationalAuthorizationQueryService operationalAuthorizationQueryService;
    private final MessageSource messageSource;

    public OperationalStatusesController(OperationalAuthorizationQueryService operationalAuthorizationQueryService,
                                         MessageSource messageSource) {
        this.operationalAuthorizationQueryService = operationalAuthorizationQueryService;
        this.messageSource = messageSource;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "List the operational status of every vehicle", description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Vehicles that were never evaluated do not appear.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current authorization of each vehicle, most recent first",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OperationalAuthorizationResource.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid status")})
    public ResponseEntity<?> getOperationalStatuses(@RequestParam(required = false) AuthorizationStatus status) {
        return ResponseEntityFromOperationalAuthorizationQueryResultAssembler.toResponseEntityFromList(
                operationalAuthorizationQueryService.handle(new GetCurrentAuthorizationsQuery(Optional.ofNullable(status))));
    }

    @GetMapping("/{vehicleId}")
    @Operation(summary = "Get the operational status of a vehicle", description = "Roles: any authenticated user of the company.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current authorization",
                    content = @Content(schema = @Schema(implementation = OperationalAuthorizationResource.class))),
            @ApiResponse(responseCode = "404", description = "The vehicle has no authorization yet")})
    public ResponseEntity<?> getOperationalStatus(@PathVariable UUID vehicleId) {
        return operationalAuthorizationQueryService.handle(new GetCurrentAuthorizationByVehicleIdQuery(new VehicleId(vehicleId)))
                .<ResponseEntity<?>>map(ResponseEntityFromOperationalAuthorizationQueryResultAssembler::toResponseEntityFromOperationalAuthorization)
                .orElseGet(() -> ResponseEntityFromOperationalAuthorizationQueryResultAssembler.notFound(messageSource, "evaluation.authorization.error.not-found"));
    }
}
