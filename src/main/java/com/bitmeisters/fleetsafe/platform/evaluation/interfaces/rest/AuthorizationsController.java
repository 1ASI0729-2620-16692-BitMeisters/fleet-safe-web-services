package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices.OperationalAuthorizationQueryService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetAuthorizationHistoryByVehicleIdQuery;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/authorizations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authorizations", description = "History of the operational authorizations of each vehicle")
public class AuthorizationsController {

    private final OperationalAuthorizationQueryService operationalAuthorizationQueryService;

    public AuthorizationsController(OperationalAuthorizationQueryService operationalAuthorizationQueryService) {
        this.operationalAuthorizationQueryService = operationalAuthorizationQueryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "List the authorization history of a vehicle", description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Most recent first.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authorization history",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OperationalAuthorizationResource.class)))),
            @ApiResponse(responseCode = "403", description = "The role does not allow this operation")})
    public ResponseEntity<?> getAuthorizations(@RequestParam UUID vehicleId) {
        return ResponseEntityFromOperationalAuthorizationQueryResultAssembler.toResponseEntityFromList(
                operationalAuthorizationQueryService.handle(new GetAuthorizationHistoryByVehicleIdQuery(new VehicleId(vehicleId))));
    }
}
