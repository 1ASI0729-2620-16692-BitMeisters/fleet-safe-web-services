package com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.bitmeisters.fleetsafe.platform.incident.application.commandservices.IncidentCommandFailure;
import com.bitmeisters.fleetsafe.platform.incident.application.commandservices.IncidentCommandService;
import com.bitmeisters.fleetsafe.platform.incident.application.internal.outboundservices.acl.ExternalIamService;
import com.bitmeisters.fleetsafe.platform.shared.application.internal.outboundservices.storage.FileStorageService;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.AddIncidentFollowUpCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.AttachCorrectiveActionEvidenceCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.CompleteRepairCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.RegisterCorrectiveActionCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.commands.ScheduleRepairCommand;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.CorrectiveAction;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.IncidentFollowUp;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.entities.Repair;
import com.bitmeisters.fleetsafe.platform.incident.domain.model.valueobjects.RepairStatus;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources.UpdateRepairResource;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.assemblers.CorrectiveActionAssembler;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.assemblers.IncidentFollowUpAssembler;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.assemblers.RepairAssembler;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources.CorrectiveActionResource;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources.IncidentFollowUpResource;
import com.bitmeisters.fleetsafe.platform.incident.interfaces.rest.resources.RepairResource;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/incidents")
@Tag(name = "Incident Operations", description = "Repairs, follow-ups and corrective actions associated with incidents.")
public class IncidentOperationsController {

    private final IncidentCommandService commandService;
    private final MessageSource messageSource;

    private final FileStorageService fileStorageService;
    private final CorrectiveActionAssembler correctiveActionAssembler;
    private final RepairAssembler repairAssembler = new RepairAssembler();
    private final IncidentFollowUpAssembler followUpAssembler;

    public IncidentOperationsController(
            IncidentCommandService commandService,
            ExternalIamService externalIamService,
            FileStorageService fileStorageService,
            MessageSource messageSource) {
        this.commandService = commandService;
        this.fileStorageService = fileStorageService;
        this.messageSource = messageSource;
        this.correctiveActionAssembler = new CorrectiveActionAssembler(
                externalIamService::nameOf, fileStorageService::temporaryReadUrl);
        this.followUpAssembler = new IncidentFollowUpAssembler(externalIamService::nameOf);
    }

    @Operation(summary = "Register a corrective action")
    @PostMapping("/{incidentId}/corrective-actions")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    public ResponseEntity<?> registerCorrectiveAction(
            @PathVariable UUID incidentId,
            @RequestBody RegisterCorrectiveActionCommand command) {

        if (!incidentId.equals(command.incidentId())) {
            return invalidIdentifierResponse();
        }

        Result<CorrectiveAction, IncidentCommandFailure> result =
                commandService.handle(command);

        if (result.isFailure()) {
            return failureResponse(result.failure().orElseThrow());
        }

        CorrectiveActionResource resource =
                correctiveActionAssembler.toResourceFromEntity(
                        result.success().orElseThrow());

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @Operation(summary = "Attach evidence to a corrective action")
    @PostMapping(value = "/{incidentId}/evidences",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    public ResponseEntity<?> attachCorrectiveActionEvidence(
            @PathVariable UUID incidentId,
            @RequestParam("correctiveActionId") UUID correctiveActionId,
            @RequestPart("file") MultipartFile file) throws IOException {

        if (!FileStorageService.SUPPORTED_CONTENT_TYPES.contains(file.getContentType())) {
            return problem(HttpStatus.BAD_REQUEST, "incident.corrective-action.error.evidence-type");
        }

        var evidenceUrl = fileStorageService.store(file.getContentType(), file.getBytes());

        if (evidenceUrl.isEmpty()) {
            return problem(HttpStatus.SERVICE_UNAVAILABLE, "incident.corrective-action.error.evidence-unavailable");
        }

        Result<CorrectiveAction, IncidentCommandFailure> result =
                commandService.handle(
                        new AttachCorrectiveActionEvidenceCommand(
                                incidentId,
                                correctiveActionId,
                                evidenceUrl.get()));

        if (result.isFailure()) {
            return failureResponse(result.failure().orElseThrow());
        }

        return ResponseEntity.ok(
                correctiveActionAssembler.toResourceFromEntity(
                        result.success().orElseThrow()));
    }

    @Operation(summary = "Register a repair")
    @PostMapping("/{incidentId}/repairs")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    public ResponseEntity<?> scheduleRepair(
            @PathVariable UUID incidentId,
            @RequestBody ScheduleRepairCommand command) {

        if (!incidentId.equals(command.incidentId())) {
            return invalidIdentifierResponse();
        }

        Result<Repair, IncidentCommandFailure> result =
                commandService.handle(command);

        if (result.isFailure()) {
            return failureResponse(result.failure().orElseThrow());
        }

        RepairResource resource =
                repairAssembler.toResourceFromEntity(
                        result.success().orElseThrow());

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @Operation(summary = "Update a repair")
    @PatchMapping("/{incidentId}/repairs/{repairId}")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    public ResponseEntity<?> completeRepair(
            @PathVariable UUID incidentId,
            @PathVariable UUID repairId,
            @RequestBody UpdateRepairResource resource) {

        if (resource.status() != RepairStatus.COMPLETED) {
            return problem(
                    HttpStatus.BAD_REQUEST,
                    "incident.repair.error.invalid-status");
        }

        if (resource.finishedAt() == null) {
            return problem(
                    HttpStatus.BAD_REQUEST,
                    "incident.repair.error.finished-at.required");
        }

        CompleteRepairCommand command =
                new CompleteRepairCommand(
                        incidentId,
                        repairId,
                        resource.finishedAt());

        Result<Repair, IncidentCommandFailure> result =
                commandService.handle(command);

        if (result.isFailure()) {
            return failureResponse(result.failure().orElseThrow());
        }

        RepairResource repairResource =
                repairAssembler.toResourceFromEntity(
                        result.success().orElseThrow());

        return ResponseEntity.ok(repairResource);
    }

    @Operation(summary = "Register an incident follow-up")
    @PostMapping("/{incidentId}/follow-ups")
    @PreAuthorize("hasAnyRole('FLEET_SUPERVISOR', 'ADMINISTRATOR')")
    public ResponseEntity<?> addFollowUp(
            @PathVariable UUID incidentId,
            @RequestBody AddIncidentFollowUpCommand command) {

        if (!incidentId.equals(command.incidentId())) {
            return invalidIdentifierResponse();
        }

        Result<IncidentFollowUp, IncidentCommandFailure> result =
                commandService.handle(command);

        if (result.isFailure()) {
            return failureResponse(result.failure().orElseThrow());
        }

        IncidentFollowUpResource resource =
                followUpAssembler.toResourceFromEntity(
                        result.success().orElseThrow());

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String messageKey) {
        String detail = messageSource.getMessage(messageKey, null, messageKey, LocaleContextHolder.getLocale());
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }

    private ResponseEntity<ProblemDetail> invalidIdentifierResponse() {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "The path identifier does not match the request body.");

        problem.setTitle("Invalid identifier");

        return ResponseEntity.badRequest().body(problem);
    }

    private ResponseEntity<ProblemDetail> failureResponse(
            IncidentCommandFailure failure) {

        HttpStatus status = failure instanceof IncidentCommandFailure.NotFound
                || failure instanceof IncidentCommandFailure.UnknownVehicle
                || failure instanceof IncidentCommandFailure.UnknownIncidentType
                || failure instanceof IncidentCommandFailure.UnknownInspection
                ? HttpStatus.NOT_FOUND
                : failure instanceof IncidentCommandFailure.InvalidStatus ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;

        String detail = messageSource.getMessage(
                failure.messageKey(),
                null,
                failure.messageKey(),
                LocaleContextHolder.getLocale());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle("Incident operation failed");

        return ResponseEntity.status(status).body(problem);
    }
}
