package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.bitmeisters.fleetsafe.platform.incident.application.commandservices.IncidentCommandFailure;
import com.bitmeisters.fleetsafe.platform.incident.application.commandservices.IncidentCommandService;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.UpdateIncidentStatusCommand;
import com.bitmeisters.fleetsafe.platform.incident.application.internal.outboundservices.acl.ExternalIamService;
import com.bitmeisters.fleetsafe.platform.shared.application.internal.outboundservices.storage.FileStorageService;
import com.bitmeisters.fleetsafe.platform.incident.application.queryservices.IncidentQueryService;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.aggregates.Incident;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.DeleteIncidentCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.ReportIncidentCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.ResolveIncidentCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.UpdateIncidentCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetCorrectiveActionsByIncidentIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentByIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentFollowUpsQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentsByStatusQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentsByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetIncidentsQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.queries.GetRepairsByIncidentIdQuery;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.IncidentStatus;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.assemblers.IncidentAssembler;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources.IncidentResource;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidents")
@Tag(name = "Incidents", description = "Registration, tracking and resolution of vehicle incidents.")
public class IncidentController {

    private final IncidentCommandService commandService;
    private final IncidentQueryService queryService;
    private final IncidentAssembler assembler;
    private final MessageSource messageSource;

    public IncidentController(
            IncidentCommandService commandService,
            IncidentQueryService queryService,
            ExternalIamService externalIamService,
            FileStorageService fileStorageService,
            MessageSource messageSource
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.messageSource = messageSource;
        this.assembler = new IncidentAssembler(externalIamService::nameOf, fileStorageService::temporaryReadUrl);
    }

    @Operation(summary = "List and filter incidents")
    @GetMapping
    public ResponseEntity<Map<String, List<IncidentResource>>> getAll(
            @RequestParam(required = false) UUID vehicleId,
            @RequestParam(required = false) IncidentStatus status
    ) {
        List<Incident> incidents = vehicleId == null
                ? queryService.handle(new GetIncidentsQuery())
                : queryService.handle(
                new GetIncidentsByVehicleIdQuery(vehicleId));

        if (status != null) {
            incidents = incidents.stream()
                    .filter(incident -> incident.getStatus() == status)
                    .toList();
        }

        List<IncidentResource> resources = incidents.stream()
                .map(this::toResource)
                .toList();

        return ResponseEntity.ok(Map.of("incidents", resources));
    }

    @Operation(summary = "Get an incident")
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable UUID id,
            Locale locale
    ) {
        return queryService.handle(new GetIncidentByIdQuery(id))
                .<ResponseEntity<?>>map(incident ->
                        ResponseEntity.ok(toResource(incident)))
                .orElseGet(() -> notFound("incident.error.not-found", locale));
    }

    @Operation(summary = "List incidents of a vehicle")
    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<Map<String, List<IncidentResource>>> getByVehicle(
            @PathVariable UUID vehicleId
    ) {
        List<IncidentResource> incidents = queryService
                .handle(new GetIncidentsByVehicleIdQuery(vehicleId))
                .stream()
                .map(this::toResource)
                .toList();

        return ResponseEntity.ok(Map.of("incidents", incidents));
    }

    @Operation(summary = "List incidents by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, List<IncidentResource>>> getByStatus(
            @PathVariable IncidentStatus status
    ) {
        List<IncidentResource> incidents = queryService
                .handle(new GetIncidentsByStatusQuery(status))
                .stream()
                .map(this::toResource)
                .toList();

        return ResponseEntity.ok(Map.of("incidents", incidents));
    }

    @Operation(summary = "Search incidents")
    @GetMapping("/search")
    public ResponseEntity<Map<String, List<IncidentResource>>> search(
            @RequestParam UUID vehicleId
    ) {
        List<IncidentResource> incidents = queryService
                .handle(new GetIncidentsByVehicleIdQuery(vehicleId))
                .stream()
                .map(this::toResource)
                .toList();

        return ResponseEntity.ok(Map.of("incidents", incidents));
    }

    @Operation(summary = "Report an incident")
    @PostMapping
    @PreAuthorize("hasAnyRole('DRIVER', 'FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    public ResponseEntity<?> report(
            @RequestBody ReportIncidentCommand command,
            Locale locale
    ) {
        Result<Incident, IncidentCommandFailure> result =
                commandService.handle(command);

        if (result.isFailure()) {
            return commandFailure(
                    result.failure().orElseThrow(),
                    locale
            );
        }

        Incident incident = result.success().orElseThrow();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResource(incident));
    }

    @Operation(summary = "Update an incident")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    public ResponseEntity<?> update(
            @PathVariable UUID id,
            @RequestBody UpdateIncidentCommand command,
            Locale locale
    ) {
        if (!id.equals(command.incidentId())) {
            return badRequest("incident.error.invalid-id", locale);
        }

        Result<Incident, IncidentCommandFailure> result =
                commandService.handle(command);

        if (result.isFailure()) {
            return commandFailure(
                    result.failure().orElseThrow(),
                    locale
            );
        }

        return ResponseEntity.ok(
                toResource(result.success().orElseThrow())
        );
    }

    @Operation(summary = "Resolve an incident")
    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    public ResponseEntity<?> resolve(
            @PathVariable UUID id,
            @RequestBody ResolveIncidentCommand command,
            Locale locale
    ) {
        if (!id.equals(command.incidentId())) {
            return badRequest("incident.error.invalid-id", locale);
        }

        Result<Incident, IncidentCommandFailure> result =
                commandService.handle(command);

        if (result.isFailure()) {
            return commandFailure(
                    result.failure().orElseThrow(),
                    locale
            );
        }

        return ResponseEntity.ok(
                toResource(result.success().orElseThrow())
        );
    }

    @Operation(summary = "Move an incident to review")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    public ResponseEntity<?> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateIncidentStatusCommand command,
            Locale locale
    ) {
        if (!id.equals(command.incidentId())) {
            return badRequest("incident.error.invalid-id", locale);
        }

        Result<Incident, IncidentCommandFailure> result =
                commandService.handle(command);

        if (result.isFailure()) {
            return commandFailure(
                    result.failure().orElseThrow(),
                    locale
            );
        }

        return ResponseEntity.ok(
                toResource(result.success().orElseThrow())
        );
    }

    @Operation(summary = "Delete an incident")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    public ResponseEntity<?> delete(
            @PathVariable UUID id,
            Locale locale
    ) {
        Result<?, IncidentCommandFailure> result =
                commandService.handle(new DeleteIncidentCommand(id));

        if (result.isFailure()) {
            return commandFailure(
                    result.failure().orElseThrow(),
                    locale
            );
        }

        return ResponseEntity.noContent().build();
    }

    private IncidentResource toResource(Incident incident) {
        UUID incidentId = incident.getId();

        return assembler.toResourceFromEntity(
                incident,
                queryService.handle(
                        new GetCorrectiveActionsByIncidentIdQuery(incidentId)
                ),
                queryService.handle(
                        new GetRepairsByIncidentIdQuery(incidentId)
                ),
                queryService.handle(
                        new GetIncidentFollowUpsQuery(incidentId)
                )
        );
    }

    private ResponseEntity<ProblemDetail> commandFailure(
            IncidentCommandFailure failure,
            Locale locale
    ) {
        HttpStatus status;

        if (failure instanceof IncidentCommandFailure.NotFound
                || failure instanceof IncidentCommandFailure.UnknownVehicle
                || failure instanceof IncidentCommandFailure.UnknownIncidentType
                || failure instanceof IncidentCommandFailure.UnknownInspection) {
            status = HttpStatus.NOT_FOUND;
        } else if (failure instanceof IncidentCommandFailure.InvalidStatus) {
            status = HttpStatus.CONFLICT;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return problem(status, failure.messageKey(), locale);
    }

    private ResponseEntity<ProblemDetail> notFound(
            String messageKey,
            Locale locale
    ) {
        return problem(HttpStatus.NOT_FOUND, messageKey, locale);
    }

    private ResponseEntity<ProblemDetail> badRequest(
            String messageKey,
            Locale locale
    ) {
        return problem(HttpStatus.BAD_REQUEST, messageKey, locale);
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
