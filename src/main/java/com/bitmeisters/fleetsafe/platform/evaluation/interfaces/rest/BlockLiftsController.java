package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.OperationalAuthorizationCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices.OperationalAuthorizationQueryService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetOverridesByPeriodQuery;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/block-lifts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Block Lifts", description = "Exceptions that let a blocked vehicle operate, with their reason and responsible user")
public class BlockLiftsController {

    private final OperationalAuthorizationCommandService operationalAuthorizationCommandService;
    private final OperationalAuthorizationQueryService operationalAuthorizationQueryService;
    private final MessageSource messageSource;

    public BlockLiftsController(OperationalAuthorizationCommandService operationalAuthorizationCommandService,
                                OperationalAuthorizationQueryService operationalAuthorizationQueryService,
                                MessageSource messageSource) {
        this.operationalAuthorizationCommandService = operationalAuthorizationCommandService;
        this.operationalAuthorizationQueryService = operationalAuthorizationQueryService;
        this.messageSource = messageSource;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Lift the block of a vehicle that is not enabled",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. The reason is required and the authenticated user is recorded as responsible.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Block lifted",
                    content = @Content(schema = @Schema(implementation = OperationalAuthorizationResource.class))),
            @ApiResponse(responseCode = "400", description = "Missing vehicle or reason"),
            @ApiResponse(responseCode = "404", description = "The vehicle has no authorization yet"),
            @ApiResponse(responseCode = "409", description = "The vehicle is not blocked")})
    public ResponseEntity<?> liftBlock(@Valid @RequestBody LiftBlockResource resource, Authentication authentication) {
        var command = LiftBlockCommandFromResourceAssembler.toCommandFromResource(resource, UUID.fromString(authentication.getName()));
        return ResponseEntityFromOperationalAuthorizationCommandResultAssembler.toResponseEntityFromResult(
                operationalAuthorizationCommandService.handle(command), messageSource, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "List the blocks lifted in a period", description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Dates in ISO-8601, for example 2026-10-01T00:00:00Z.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lifted blocks, most recent first",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = OperationalAuthorizationResource.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid period")})
    public ResponseEntity<?> getBlockLifts(@RequestParam Instant from, @RequestParam Instant to) {
        return ResponseEntityFromOperationalAuthorizationQueryResultAssembler.toResponseEntityFromList(
                operationalAuthorizationQueryService.handle(new GetOverridesByPeriodQuery(from, to)));
    }
}
