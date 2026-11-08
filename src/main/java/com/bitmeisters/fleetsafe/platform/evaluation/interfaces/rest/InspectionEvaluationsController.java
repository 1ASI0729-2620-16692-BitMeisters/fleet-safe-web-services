package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.application.internal.outboundservices.acl.ExternalInspectionService;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform.ResponseEntityFromEvaluationCommandResultAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/inspections/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Operational Authorizations", description = "Whether a vehicle may operate, its history and the lifted blocks")
public class InspectionEvaluationsController {

    private final ExternalInspectionService externalInspectionService;
    private final EvaluationCommandService evaluationCommandService;
    private final MessageSource messageSource;

    public InspectionEvaluationsController(ExternalInspectionService externalInspectionService,
                                           EvaluationCommandService evaluationCommandService, MessageSource messageSource) {
        this.externalInspectionService = externalInspectionService;
        this.evaluationCommandService = evaluationCommandService;
        this.messageSource = messageSource;
    }

    @PostMapping("/evaluate")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    @Operation(summary = "Evaluate a completed inspection",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR. Completed inspections are evaluated automatically; this evaluates one that was not.")
    public ResponseEntity<?> evaluateInspection(@PathVariable UUID id, Authentication authentication) {
        return externalInspectionService.evaluationOf(id, UUID.fromString(authentication.getName()))
                .<ResponseEntity<?>>map(command -> ResponseEntityFromEvaluationCommandResultAssembler.toResponseEntityFromResult(
                        evaluationCommandService.handle(command), messageSource, HttpStatus.OK))
                .orElseGet(() -> {
                    var detail = messageSource.getMessage("evaluation.error.inspection-not-found", null,
                            "evaluation.error.inspection-not-found", LocaleContextHolder.getLocale());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail));
                });
    }
}
