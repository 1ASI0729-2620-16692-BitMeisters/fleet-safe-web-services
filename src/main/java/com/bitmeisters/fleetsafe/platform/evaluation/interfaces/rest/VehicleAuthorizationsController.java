package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.OperationalAuthorizationCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices.OperationalAuthorizationQueryService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetAuthorizationHistoryByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetCurrentAuthorizationByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources.LiftBlockResource;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources.OperationalAuthorizationResource;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform.LiftBlockCommandFromResourceAssembler;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform.ResponseEntityFromOperationalAuthorizationCommandResultAssembler;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform.ResponseEntityFromOperationalAuthorizationQueryResultAssembler;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/vehicles/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Operational Authorizations", description = "Whether a vehicle may operate, its history and the lifted blocks")
public class VehicleAuthorizationsController {

    private final OperationalAuthorizationCommandService operationalAuthorizationCommandService;
    private final OperationalAuthorizationQueryService operationalAuthorizationQueryService;
    private final MessageSource messageSource;

    public VehicleAuthorizationsController(OperationalAuthorizationCommandService operationalAuthorizationCommandService,
                                           OperationalAuthorizationQueryService operationalAuthorizationQueryService,
                                           MessageSource messageSource) {
        this.operationalAuthorizationCommandService = operationalAuthorizationCommandService;
        this.operationalAuthorizationQueryService = operationalAuthorizationQueryService;
        this.messageSource = messageSource;
    }

    @GetMapping("/status")
    @Operation(summary = "Get the current operational status of a vehicle", description = "Roles: any authenticated user of the company.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current authorization",
                    content = @Content(schema = @Schema(implementation = OperationalAuthorizationResource.class))),
            @ApiResponse(responseCode = "404", description = "The vehicle has no authorization yet")})
    public ResponseEntity<?> getCurrentAuthorization(@PathVariable UUID id) {
        return operationalAuthorizationQueryService.handle(new GetCurrentAuthorizationByVehicleIdQuery(new VehicleId(id)))
                .<ResponseEntity<?>>map(ResponseEntityFromOperationalAuthorizationQueryResultAssembler::toResponseEntityFromOperationalAuthorization)
                .orElseGet(() -> ResponseEntityFromOperationalAuthorizationQueryResultAssembler.notFound(messageSource, "evaluation.authorization.error.not-found"));
    }

    @GetMapping("/authorizations")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Get the authorization history of a vehicle", description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Most recent first.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authorization history",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OperationalAuthorizationResource.class)))),
            @ApiResponse(responseCode = "403", description = "The role does not allow this operation")})
    public ResponseEntity<?> getAuthorizationHistory(@PathVariable UUID id) {
        return ResponseEntityFromOperationalAuthorizationQueryResultAssembler.toResponseEntityFromList(
                operationalAuthorizationQueryService.handle(new GetAuthorizationHistoryByVehicleIdQuery(new VehicleId(id))));
    }

    @PostMapping("/block-lifts")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Lift the block of a vehicle that is not enabled",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. The reason is required and the authenticated user is recorded as responsible.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Block lifted",
                    content = @Content(schema = @Schema(implementation = OperationalAuthorizationResource.class))),
            @ApiResponse(responseCode = "400", description = "Missing reason"),
            @ApiResponse(responseCode = "404", description = "The vehicle has no authorization yet"),
            @ApiResponse(responseCode = "409", description = "The vehicle is not blocked")})
    public ResponseEntity<?> liftBlock(@PathVariable UUID id, @Valid @RequestBody LiftBlockResource resource, Authentication authentication) {
        var command = LiftBlockCommandFromResourceAssembler.toCommandFromResource(id, resource, UUID.fromString(authentication.getName()));
        return ResponseEntityFromOperationalAuthorizationCommandResultAssembler.toResponseEntityFromResult(
                operationalAuthorizationCommandService.handle(command), messageSource, HttpStatus.CREATED);
    }
}
