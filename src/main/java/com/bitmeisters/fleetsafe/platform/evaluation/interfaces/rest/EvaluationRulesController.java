package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationRuleCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices.EvaluationRuleQueryService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DeactivateEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetActiveEvaluationRulesQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetEvaluationRuleByIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources.DefineEvaluationRuleResource;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources.EvaluationRuleResource;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform.DefineEvaluationRuleCommandFromResourceAssembler;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform.ResponseEntityFromEvaluationRuleCommandResultAssembler;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform.ResponseEntityFromEvaluationRuleQueryResultAssembler;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/evaluation-rules", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Evaluation Rules", description = "Rules that turn inspection results into an impact on the vehicle")
public class EvaluationRulesController {

    private final EvaluationRuleCommandService evaluationRuleCommandService;
    private final EvaluationRuleQueryService evaluationRuleQueryService;
    private final MessageSource messageSource;

    public EvaluationRulesController(EvaluationRuleCommandService evaluationRuleCommandService,
                                     EvaluationRuleQueryService evaluationRuleQueryService,
                                     MessageSource messageSource) {
        this.evaluationRuleCommandService = evaluationRuleCommandService;
        this.evaluationRuleQueryService = evaluationRuleQueryService;
        this.messageSource = messageSource;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "List the active evaluation rules", description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active rules",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EvaluationRuleResource.class)))),
            @ApiResponse(responseCode = "403", description = "The role does not allow this operation")})
    public ResponseEntity<?> getActiveEvaluationRules() {
        return ResponseEntityFromEvaluationRuleQueryResultAssembler.toResponseEntityFromList(
                evaluationRuleQueryService.handle(new GetActiveEvaluationRulesQuery()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Define an evaluation rule", description = "Roles: ADMINISTRATOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rule defined", content = @Content(schema = @Schema(implementation = EvaluationRuleResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "409", description = "An active rule already exists for that item and result")})
    public ResponseEntity<?> defineEvaluationRule(@Valid @RequestBody DefineEvaluationRuleResource resource) {
        var result = evaluationRuleCommandService.handle(DefineEvaluationRuleCommandFromResourceAssembler.toCommandFromResource(resource));
        return ResponseEntityFromEvaluationRuleCommandResultAssembler.toResponseEntityFromResult(result, messageSource, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Get an evaluation rule", description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The rule", content = @Content(schema = @Schema(implementation = EvaluationRuleResource.class))),
            @ApiResponse(responseCode = "404", description = "Rule not found")})
    public ResponseEntity<?> getEvaluationRuleById(@PathVariable UUID id) {
        return evaluationRuleQueryService.handle(new GetEvaluationRuleByIdQuery(id))
                .<ResponseEntity<?>>map(ResponseEntityFromEvaluationRuleQueryResultAssembler::toResponseEntityFromEvaluationRule)
                .orElseGet(() -> ResponseEntityFromEvaluationRuleQueryResultAssembler.notFound(messageSource, "evaluation.rule.error.not-found"));
    }

    @PostMapping("/{id}/deactivation")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(summary = "Deactivate an evaluation rule", description = "Roles: ADMINISTRATOR. Deactivated rules are no longer applied.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rule deactivated", content = @Content(schema = @Schema(implementation = EvaluationRuleResource.class))),
            @ApiResponse(responseCode = "404", description = "Rule not found"),
            @ApiResponse(responseCode = "409", description = "The rule is already inactive")})
    public ResponseEntity<?> deactivateEvaluationRule(@PathVariable UUID id) {
        var result = evaluationRuleCommandService.handle(new DeactivateEvaluationRuleCommand(id));
        return ResponseEntityFromEvaluationRuleCommandResultAssembler.toResponseEntityFromResult(result, messageSource, HttpStatus.OK);
    }
}
