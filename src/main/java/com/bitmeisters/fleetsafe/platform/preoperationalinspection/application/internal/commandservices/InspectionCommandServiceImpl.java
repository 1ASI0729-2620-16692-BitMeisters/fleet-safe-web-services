package com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices.InspectionCommandFailure;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices.InspectionCommandService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.Inspection;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.AddObservationCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.AttachEvidenceCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.CompleteInspectionCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.RegisterInspectionResultCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.StartInspectionCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.UpdateInspectionResultCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionStatus;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.repositories.InspectionItemRepository;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.repositories.InspectionRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the InspectionCommandService port.
 *
 * <p>
 * Coordinates vehicle pre-operational inspection workflows, validates business invariants,
 * records results, and commits state transitions.
 * </p>
 */
@Slf4j
@Service
public class InspectionCommandServiceImpl implements InspectionCommandService {

    private final InspectionRepository inspectionRepository;
    private final InspectionItemRepository inspectionItemRepository;

    /**
     * Constructs the command service with its required repository dependencies.
     *
     * @param inspectionRepository     the inspection domain repository
     * @param inspectionItemRepository the catalog items domain repository
     */
    public InspectionCommandServiceImpl(InspectionRepository inspectionRepository,
                                       InspectionItemRepository inspectionItemRepository) {
        this.inspectionRepository = inspectionRepository;
        this.inspectionItemRepository = inspectionItemRepository;
    }

    @Override
    @Transactional
    public Result<Inspection, InspectionCommandFailure> handle(StartInspectionCommand command) {
        if (inspectionRepository.existsByVehicleIdAndStatus(command.vehicleId(), InspectionStatus.IN_PROGRESS)) {
            log.warn("Vehicle already has an ongoing inspection: vehicleId={}", command.vehicleId().value());
            return Result.failure(new InspectionCommandFailure.VehicleHasOngoingInspection());
        }

        var inspection = new Inspection(command.vehicleId(), command.driverId(), command.odometer());
        var saved = inspectionRepository.save(inspection);
        log.info("Started pre-operational inspection: id={}, vehicleId={}", saved.getId().value(), command.vehicleId().value());
        return Result.success(saved);
    }

    @Override
    @Transactional
    public Result<Inspection, InspectionCommandFailure> handle(RegisterInspectionResultCommand command) {
        var inspectionOpt = inspectionRepository.findById(command.inspectionId());
        if (inspectionOpt.isEmpty()) {
            log.warn("Inspection not found: id={}", command.inspectionId().value());
            return Result.failure(new InspectionCommandFailure.NotFound());
        }

        var inspection = inspectionOpt.get();
        if (inspection.getStatus() != InspectionStatus.IN_PROGRESS) {
            log.warn("Inspection is not in progress: id={}, status={}", inspection.getId().value(), inspection.getStatus());
            return Result.failure(new InspectionCommandFailure.NotInProgress());
        }

        var itemOpt = inspectionItemRepository.findById(command.itemId());
        if (itemOpt.isEmpty()) {
            log.warn("Inspection item not found in catalog: itemId={}", command.itemId().value());
            return Result.failure(new InspectionCommandFailure.ItemNotFound());
        }

        var item = itemOpt.get();
        try {
            inspection.registerResult(command.itemId(), item.getName(), item.getCategory(), command.resultValue());
            var saved = inspectionRepository.save(inspection);
            log.info("Registered inspection result: inspectionId={}, itemId={}, result={}",
                    saved.getId().value(), item.getId().value(), command.resultValue());
            return Result.success(saved);
        } catch (IllegalArgumentException ex) {
            if (ex.getMessage().contains("item-already-registered")) {
                return Result.failure(new InspectionCommandFailure.ItemAlreadyRegistered());
            }
            return Result.failure(new InspectionCommandFailure.NotInProgress());
        }
    }

    @Override
    @Transactional
    public Result<Inspection, InspectionCommandFailure> handle(UpdateInspectionResultCommand command) {
        var inspectionOpt = inspectionRepository.findById(command.inspectionId());
        if (inspectionOpt.isEmpty()) {
            log.warn("Inspection not found: id={}", command.inspectionId().value());
            return Result.failure(new InspectionCommandFailure.NotFound());
        }

        var inspection = inspectionOpt.get();
        if (inspection.getStatus() != InspectionStatus.IN_PROGRESS) {
            log.warn("Inspection is not in progress: id={}", inspection.getId().value());
            return Result.failure(new InspectionCommandFailure.NotInProgress());
        }

        try {
            inspection.updateResult(command.resultId(), command.resultValue());
            var saved = inspectionRepository.save(inspection);
            log.info("Updated inspection result: inspectionId={}, resultId={}, newResult={}",
                    saved.getId().value(), command.resultId().value(), command.resultValue());
            return Result.success(saved);
        } catch (IllegalArgumentException ex) {
            return Result.failure(new InspectionCommandFailure.ResultNotFound());
        }
    }

    @Override
    @Transactional
    public Result<Inspection, InspectionCommandFailure> handle(AddObservationCommand command) {
        var inspectionOpt = inspectionRepository.findById(command.inspectionId());
        if (inspectionOpt.isEmpty()) {
            log.warn("Inspection not found: id={}", command.inspectionId().value());
            return Result.failure(new InspectionCommandFailure.NotFound());
        }

        var inspection = inspectionOpt.get();
        if (inspection.getStatus() != InspectionStatus.IN_PROGRESS) {
            log.warn("Inspection is not in progress: id={}", inspection.getId().value());
            return Result.failure(new InspectionCommandFailure.NotInProgress());
        }

        try {
            inspection.addObservationToResult(command.resultId(), command.description(), command.createdBy());
            var saved = inspectionRepository.save(inspection);
            log.info("Added observation to result: inspectionId={}, resultId={}", saved.getId().value(), command.resultId().value());
            return Result.success(saved);
        } catch (IllegalArgumentException ex) {
            return Result.failure(new InspectionCommandFailure.ResultNotFound());
        }
    }

    @Override
    @Transactional
    public Result<Inspection, InspectionCommandFailure> handle(AttachEvidenceCommand command) {
        var inspectionOpt = inspectionRepository.findById(command.inspectionId());
        if (inspectionOpt.isEmpty()) {
            log.warn("Inspection not found: id={}", command.inspectionId().value());
            return Result.failure(new InspectionCommandFailure.NotFound());
        }

        var inspection = inspectionOpt.get();
        if (inspection.getStatus() != InspectionStatus.IN_PROGRESS) {
            log.warn("Inspection is not in progress: id={}", inspection.getId().value());
            return Result.failure(new InspectionCommandFailure.NotInProgress());
        }

        try {
            inspection.attachEvidenceToObservation(command.resultId(), command.observationId(), command.fileUrl(), command.mediaType());
            var saved = inspectionRepository.save(inspection);
            log.info("Attached evidence to observation: inspectionId={}, observationId={}", saved.getId().value(), command.observationId().value());
            return Result.success(saved);
        } catch (IllegalArgumentException ex) {
            if (ex.getMessage().contains("observation-not-found")) {
                return Result.failure(new InspectionCommandFailure.ObservationNotFound());
            }
            return Result.failure(new InspectionCommandFailure.ResultNotFound());
        }
    }

    @Override
    @Transactional
    public Result<Inspection, InspectionCommandFailure> handle(CompleteInspectionCommand command) {
        var inspectionOpt = inspectionRepository.findById(command.inspectionId());
        if (inspectionOpt.isEmpty()) {
            log.warn("Inspection not found: id={}", command.inspectionId().value());
            return Result.failure(new InspectionCommandFailure.NotFound());
        }

        var inspection = inspectionOpt.get();
        if (inspection.getStatus() != InspectionStatus.IN_PROGRESS) {
            log.warn("Inspection is already completed: id={}", inspection.getId().value());
            return Result.failure(new InspectionCommandFailure.NotInProgress());
        }

        var activeCatalogItems = inspectionItemRepository.findAllByActiveTrueOrderByDisplayOrderAsc();

        try {
            inspection.complete(activeCatalogItems);
            var saved = inspectionRepository.save(inspection);
            log.info("Completed inspection and triggered evaluations: id={}", saved.getId().value());
            return Result.success(saved);
        } catch (IllegalArgumentException ex) {
            if (ex.getMessage().contains("missing-items")) {
                return Result.failure(new InspectionCommandFailure.MissingItems());
            }
            if (ex.getMessage().contains("missing-observation")) {
                return Result.failure(new InspectionCommandFailure.MissingObservation());
            }
            if (ex.getMessage().contains("missing-evidence")) {
                return Result.failure(new InspectionCommandFailure.MissingEvidence());
            }
            return Result.failure(new InspectionCommandFailure.NotInProgress());
        }
    }
}
