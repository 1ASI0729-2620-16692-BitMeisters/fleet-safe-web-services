package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.bitmeisters.fleetsafe.platform.incident.application.commandservices.IncidentTypeCommandFailure;
import com.bitmeisters.fleetsafe.platform.incident.application.commandservices.IncidentTypeCommandService;
import com.bitmeisters.fleetsafe.platform.incident.application.queryservices.IncidentTypeQueryService;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.IncidentType;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.RegisterIncidentTypeCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.UpdateIncidentTypeCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentTypeByIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentTypesQuery;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.assemblers.IncidentTypeAssembler;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources.IncidentTypeResource;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incident-types")
@Tag(name = "Incident Types", description = "Catalog of the types of incidents that may affect a vehicle.")
public class IncidentTypeController {

    private final IncidentTypeCommandService commandService;
    private final IncidentTypeQueryService queryService;
    private final IncidentTypeAssembler assembler;
    private final MessageSource messageSource;

    public IncidentTypeController(
            IncidentTypeCommandService commandService,
            IncidentTypeQueryService queryService,
            MessageSource messageSource
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.messageSource = messageSource;
        this.assembler = new IncidentTypeAssembler();
    }

    @Operation(summary = "List incident types")
    @GetMapping
    public ResponseEntity<Map<String, List<IncidentTypeResource>>> getAll(
            @RequestParam(defaultValue = "false") boolean activeOnly
    ) {
        List<IncidentTypeResource> incidentTypes = queryService
                .handle(new GetIncidentTypesQuery(activeOnly))
                .stream()
                .map(assembler::toResourceFromEntity)
                .toList();

        return ResponseEntity.ok(Map.of("incidentTypes", incidentTypes));
    }

    @Operation(summary = "Get an incident type")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable UUID id,
            Locale locale
    ) {
        return queryService.handle(new GetIncidentTypeByIdQuery(id))
                .<ResponseEntity<?>>map(incidentType ->
                        ResponseEntity.ok(
                                assembler.toResourceFromEntity(incidentType)
                        ))
                .orElseGet(() -> problem(
                        HttpStatus.NOT_FOUND,
                        "incident.type.error.not-found",
                        locale
                ));
    }

    @Operation(summary = "Register an incident type")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<?> register(
            @RequestBody RegisterIncidentTypeCommand command,
            Locale locale
    ) {
        Result<IncidentType, IncidentTypeCommandFailure> result =
                commandService.handle(command);

        if (result.isFailure()) {
            return commandFailure(
                    result.failure().orElseThrow(),
                    locale
            );
        }

        IncidentType incidentType = result.success().orElseThrow();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(assembler.toResourceFromEntity(incidentType));
    }

    @Operation(summary = "Update an incident type")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<?> update(
            @PathVariable UUID id,
            @RequestBody UpdateIncidentTypeCommand command,
            Locale locale
    ) {
        if (!id.equals(command.incidentTypeId())) {
            return problem(
                    HttpStatus.BAD_REQUEST,
                    "incident.type.error.invalid-id",
                    locale
            );
        }

        Result<IncidentType, IncidentTypeCommandFailure> result =
                commandService.handle(command);

        if (result.isFailure()) {
            return commandFailure(
                    result.failure().orElseThrow(),
                    locale
            );
        }

        return ResponseEntity.ok(
                assembler.toResourceFromEntity(
                        result.success().orElseThrow()
                )
        );
    }

    private ResponseEntity<ProblemDetail> commandFailure(
            IncidentTypeCommandFailure failure,
            Locale locale
    ) {
        HttpStatus status;

        if (failure instanceof IncidentTypeCommandFailure.NotFound) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.CONFLICT;
        }

        return problem(status, failure.messageKey(), locale);
    }

    private ResponseEntity<ProblemDetail> problem(
            HttpStatus status,
            String messageKey,
            Locale locale
    ) {
        String detail = messageSource.getMessage(
                messageKey,
                null,
                messageKey,
                locale
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status,
                detail
        );

        problem.setTitle(status.getReasonPhrase());
        problem.setProperty("messageKey", messageKey);

        return ResponseEntity.status(status).body(problem);
    }
}
