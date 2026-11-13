package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices.InspectionCommandService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.internal.outboundservices.storage.EvidenceStorageService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.queryservices.InspectionQueryService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.Inspection;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.AddObservationCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.AttachEvidenceCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.CompleteInspectionCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.RegisterInspectionResultCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.StartInspectionCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.UpdateInspectionResultCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.entities.InspectionResult;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.entities.Observation;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionByIdQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionsByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionsQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.MediaType;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ObservationDescription;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ObservationId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.Odometer;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.AddObservationResource;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.CompleteInspectionResource;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.InspectionResource;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.RegisterInspectionResultResource;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.StartInspectionResource;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.resources.UpdateInspectionResultResource;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform.InspectionResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

import static com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform.ResponseEntityFromInspectionCommandResultAssembler.toProblem;
import static com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.rest.transform.ResponseEntityFromInspectionCommandResultAssembler.toResponseEntityFromResult;

@RestController
@RequestMapping(value = "/api/v1/inspections", produces = org.springframework.http.MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Inspections", description = "Pre-operational inspections, their results, observations and evidences")
public class InspectionsController {

    private static final String DRIVER = "ROLE_DRIVER";

    private final InspectionCommandService inspectionCommandService;
    private final InspectionQueryService inspectionQueryService;
    private final EvidenceStorageService evidenceStorageService;
    private final MessageSource messageSource;

    public InspectionsController(InspectionCommandService inspectionCommandService, InspectionQueryService inspectionQueryService,
                                 EvidenceStorageService evidenceStorageService, MessageSource messageSource) {
        this.inspectionCommandService = inspectionCommandService;
        this.inspectionQueryService = inspectionQueryService;
        this.evidenceStorageService = evidenceStorageService;
        this.messageSource = messageSource;
    }

    @GetMapping
    @Operation(summary = "List inspections", description = "Roles: FLEET_SUPERVISOR and ADMINISTRATOR see every inspection; DRIVER only sees their own.")
    public ResponseEntity<?> getInspections(@RequestParam(required = false) UUID vehicleId, Authentication authentication) {
        var inspections = vehicleId == null
                ? inspectionQueryService.handle(new GetInspectionsQuery())
                : inspectionQueryService.handle(new GetInspectionsByVehicleIdQuery(new VehicleId(vehicleId)));
        return ResponseEntity.ok(inspections.stream()
                .filter(inspection -> !isDriver(authentication) || isPerformedBy(inspection, authentication))
                .sorted(Comparator.comparing(Inspection::getStartedAt).reversed())
                .map(this::toResource)
                .toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Start a pre-operational inspection", description = "Roles: DRIVER. The driver comes from the token.")
    public ResponseEntity<?> startInspection(@Valid @RequestBody StartInspectionResource resource, Authentication authentication) {
        var command = new StartInspectionCommand(new VehicleId(resource.vehicleId()), new DriverId(userId(authentication)),
                new Odometer(resource.odometer()));
        return toResponseEntityFromResult(inspectionCommandService.handle(command), this::toResource, messageSource, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an inspection with its results, observations and evidences",
            description = "Roles: FLEET_SUPERVISOR, ADMINISTRATOR, or the DRIVER who performed it.")
    public ResponseEntity<?> getInspection(@PathVariable UUID id, Authentication authentication) {
        return accessibleInspection(id, authentication, false)
                .<ResponseEntity<?>>map(inspection -> ResponseEntity.ok(toResource(inspection)))
                .orElseGet(() -> deniedOrMissing(id));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Complete an inspection", description = "Roles: the DRIVER who performed it. The only accepted change is status COMPLETED.")
    public ResponseEntity<?> completeInspection(@PathVariable UUID id, @Valid @RequestBody CompleteInspectionResource resource,
                                                Authentication authentication) {
        if (accessibleInspection(id, authentication, true).isEmpty()) return deniedOrMissing(id);
        return toResponseEntityFromResult(inspectionCommandService.handle(new CompleteInspectionCommand(new InspectionId(id))),
                this::toResource, messageSource, HttpStatus.OK);
    }

    @PostMapping("/{id}/results")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Register the result of an inspection item", description = "Roles: the DRIVER who performed it, while the inspection is IN_PROGRESS.")
    public ResponseEntity<?> registerResult(@PathVariable UUID id, @Valid @RequestBody RegisterInspectionResultResource resource,
                                            Authentication authentication) {
        if (accessibleInspection(id, authentication, true).isEmpty()) return deniedOrMissing(id);
        var itemId = new InspectionItemId(resource.inspectionItemId());
        var command = new RegisterInspectionResultCommand(new InspectionId(id), itemId, ResultValue.valueOf(resource.result()));
        return toResponseEntityFromResult(inspectionCommandService.handle(command),
                inspection -> resultResource(inspection.getResults().stream()
                        .filter(result -> itemId.equals(result.getInspectionItemId())).findFirst().orElseThrow()),
                messageSource, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/results/{resultId}")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Change the result of an inspection item", description = "Roles: the DRIVER who performed it, while the inspection is IN_PROGRESS.")
    public ResponseEntity<?> updateResult(@PathVariable UUID id, @PathVariable UUID resultId,
                                          @Valid @RequestBody UpdateInspectionResultResource resource, Authentication authentication) {
        if (accessibleInspection(id, authentication, true).isEmpty()) return deniedOrMissing(id);
        var command = new UpdateInspectionResultCommand(new InspectionId(id), new InspectionResultId(resultId),
                ResultValue.valueOf(resource.result()));
        return toResponseEntityFromResult(inspectionCommandService.handle(command),
                inspection -> resultResource(findResult(inspection, resultId)), messageSource, HttpStatus.OK);
    }

    @PostMapping("/{id}/observations")
    @PreAuthorize("hasAnyRole('DRIVER', 'FLEET_SUPERVISOR')")
    @Operation(summary = "Add an observation to a result",
            description = "Roles: the DRIVER who performed the inspection, or a FLEET_SUPERVISOR, while it is IN_PROGRESS.")
    public ResponseEntity<?> addObservation(@PathVariable UUID id, @Valid @RequestBody AddObservationResource resource,
                                            Authentication authentication) {
        if (accessibleInspection(id, authentication, true).isEmpty()) return deniedOrMissing(id);
        var resultId = resource.resultId();
        var command = new AddObservationCommand(new InspectionId(id), new InspectionResultId(resultId),
                new ObservationDescription(resource.description()), userId(authentication));
        return toResponseEntityFromResult(inspectionCommandService.handle(command),
                inspection -> InspectionResourceFromEntityAssembler.toResourceFromEntity(
                        latestObservation(findResult(inspection, resultId)), evidenceStorageService::temporaryReadUrl),
                messageSource, HttpStatus.CREATED);
    }

    @PostMapping(value = "/{id}/evidences",
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Attach a photo as evidence of an observation",
            description = "Roles: the DRIVER who performed the inspection, while it is IN_PROGRESS. JPEG or PNG, up to 5 MB, with the observation it documents.")
    public ResponseEntity<?> attachEvidence(@PathVariable UUID id, @RequestParam UUID observationId,
                                            @RequestPart("file") MultipartFile file, Authentication authentication) throws IOException {
        var accessible = accessibleInspection(id, authentication, true);
        if (accessible.isEmpty()) return deniedOrMissing(id);
        var observation = new ObservationId(observationId);
        var owner = accessible.get().getResults().stream()
                .filter(result -> result.getObservations().stream().anyMatch(candidate -> observation.equals(candidate.getId())))
                .findFirst();
        if (owner.isEmpty()) return toProblem(HttpStatus.NOT_FOUND, "inspection.error.observation-not-found", messageSource);
        var resultId = owner.get().getId().value();
        var mediaType = MediaType.fromString(String.valueOf(file.getContentType()));
        var fileUrl = evidenceStorageService.store(mediaType.getValue(), file.getBytes());
        if (fileUrl.isEmpty()) return toProblem(HttpStatus.SERVICE_UNAVAILABLE, "inspection.evidence.error.storage-unavailable", messageSource);
        var command = new AttachEvidenceCommand(new InspectionId(id), new InspectionResultId(resultId), new ObservationId(observationId),
                fileUrl.get(), mediaType);
        return toResponseEntityFromResult(inspectionCommandService.handle(command),
                inspection -> findObservation(findResult(inspection, resultId), observationId).getEvidences().stream()
                        .filter(evidence -> fileUrl.get().equals(evidence.getFileUrl()))
                        .findFirst()
                        .map(evidence -> InspectionResourceFromEntityAssembler.toResourceFromEntity(evidence, evidenceStorageService::temporaryReadUrl))
                        .orElseThrow(),
                messageSource, HttpStatus.CREATED);
    }

    private Optional<Inspection> accessibleInspection(UUID id, Authentication authentication, boolean writing) {
        return inspectionQueryService.handle(new GetInspectionByIdQuery(new InspectionId(id)))
                .filter(inspection -> isDriver(authentication) ? isPerformedBy(inspection, authentication) : !writing || isSupervisor(authentication));
    }

    private ResponseEntity<?> deniedOrMissing(UUID id) {
        return inspectionQueryService.handle(new GetInspectionByIdQuery(new InspectionId(id))).isPresent()
                ? toProblem(HttpStatus.FORBIDDEN, "iam.error.forbidden", messageSource)
                : toProblem(HttpStatus.NOT_FOUND, "inspection.error.not-found", messageSource);
    }

    private InspectionResource toResource(Inspection inspection) {
        return InspectionResourceFromEntityAssembler.toResourceFromEntity(inspection, evidenceStorageService::temporaryReadUrl);
    }

    private Object resultResource(InspectionResult result) {
        return InspectionResourceFromEntityAssembler.toResourceFromEntity(result, evidenceStorageService::temporaryReadUrl);
    }

    private static InspectionResult findResult(Inspection inspection, UUID resultId) {
        var id = new InspectionResultId(resultId);
        return inspection.getResults().stream().filter(result -> id.equals(result.getId())).findFirst().orElseThrow();
    }

    private static Observation findObservation(InspectionResult result, UUID observationId) {
        var id = new ObservationId(observationId);
        return result.getObservations().stream().filter(observation -> id.equals(observation.getId())).findFirst().orElseThrow();
    }

    private static Observation latestObservation(InspectionResult result) {
        return result.getObservations().stream().max(Comparator.comparing(Observation::getCreatedAt)).orElseThrow();
    }

    private static boolean isDriver(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority -> DRIVER.equals(authority.getAuthority()));
    }

    private static boolean isSupervisor(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(authority -> "ROLE_FLEET_SUPERVISOR".equals(authority.getAuthority()));
    }

    private static boolean isPerformedBy(Inspection inspection, Authentication authentication) {
        return inspection.getDriverId().value().equals(userId(authentication));
    }

    private static UUID userId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }
}
