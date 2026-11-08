package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices.OperationalAuthorizationQueryService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetCurrentAuthorizationsQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetOverridesByPeriodQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.AuthorizationStatus;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources.OperationalAuthorizationResource;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform.ResponseEntityFromOperationalAuthorizationQueryResultAssembler;
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

import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/operational-authorizations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Operational Authorizations", description = "Whether a vehicle may operate, its history and the lifted blocks")
public class OperationalAuthorizationsController {

    private final OperationalAuthorizationQueryService operationalAuthorizationQueryService;

    public OperationalAuthorizationsController(OperationalAuthorizationQueryService operationalAuthorizationQueryService) {
        this.operationalAuthorizationQueryService = operationalAuthorizationQueryService;
    }

    @GetMapping("/current")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "List the current operational status of every vehicle", description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Vehicles that were never evaluated do not appear.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current authorization of each vehicle, most recent first",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OperationalAuthorizationResource.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid status")})
    public ResponseEntity<?> getCurrentAuthorizations(@RequestParam(required = false) AuthorizationStatus status) {
        return ResponseEntityFromOperationalAuthorizationQueryResultAssembler.toResponseEntityFromList(
                operationalAuthorizationQueryService.handle(new GetCurrentAuthorizationsQuery(Optional.ofNullable(status))));
    }

    @GetMapping("/overrides")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "List the blocks lifted in a period", description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Dates in ISO-8601, for example 2026-10-01T00:00:00Z.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lifted blocks, most recent first",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OperationalAuthorizationResource.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid period")})
    public ResponseEntity<?> getOverridesByPeriod(@RequestParam Instant from, @RequestParam Instant to) {
        return ResponseEntityFromOperationalAuthorizationQueryResultAssembler.toResponseEntityFromList(
                operationalAuthorizationQueryService.handle(new GetOverridesByPeriodQuery(from, to)));
    }
}
