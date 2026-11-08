package com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices.InspectionItemCommandFailure;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices.InspectionItemCommandService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.InspectionItem;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.CreateInspectionItemCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.DeactivateInspectionItemCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.UpdateInspectionItemCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.repositories.InspectionItemRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the InspectionItemCommandService.
 *
 * <p>
 * Coordinates business operations, invariant verifications, and persistence
 * for pre-operational inspection items.
 * </p>
 */
@Slf4j
@Service
public class InspectionItemCommandServiceImpl implements InspectionItemCommandService {

    private final InspectionItemRepository inspectionItemRepository;

    /**
     * Constructs the command service with its required repository dependency.
     *
     * @param inspectionItemRepository the domain repository
     */
    public InspectionItemCommandServiceImpl(InspectionItemRepository inspectionItemRepository) {
        this.inspectionItemRepository = inspectionItemRepository;
    }

    @Override
    @Transactional
    public Result<InspectionItem, InspectionItemCommandFailure> handle(CreateInspectionItemCommand command) {
        if (inspectionItemRepository.existsByCode(command.code())) {
            log.warn("Inspection item code already exists: code={}", command.code().value());
            return Result.failure(new InspectionItemCommandFailure.DuplicateCode());
        }

        var item = new InspectionItem(
                command.code(),
                command.name(),
                command.description(),
                command.category(),
                command.system(),
                command.safetyComponent(),
                command.requiresEvidence(),
                command.displayOrder());

        var savedItem = inspectionItemRepository.save(item);
        log.info("Inspection item created successfully: id={}, code={}", savedItem.getId().value(), savedItem.getCode().value());
        return Result.success(savedItem);
    }

    @Override
    @Transactional
    public Result<InspectionItem, InspectionItemCommandFailure> handle(UpdateInspectionItemCommand command) {
        var existingItem = inspectionItemRepository.findById(command.id());
        if (existingItem.isEmpty()) {
            log.warn("Inspection item not found for update: id={}", command.id().value());
            return Result.failure(new InspectionItemCommandFailure.NotFound());
        }

        var item = existingItem.get();
        item.update(
                command.name(),
                command.description(),
                command.category(),
                command.system(),
                command.safetyComponent(),
                command.requiresEvidence(),
                command.displayOrder());

        var updatedItem = inspectionItemRepository.save(item);
        log.info("Inspection item updated successfully: id={}, code={}", updatedItem.getId().value(), updatedItem.getCode().value());
        return Result.success(updatedItem);
    }

    @Override
    @Transactional
    public Result<InspectionItem, InspectionItemCommandFailure> handle(DeactivateInspectionItemCommand command) {
        var existingItem = inspectionItemRepository.findById(command.id());
        if (existingItem.isEmpty()) {
            log.warn("Inspection item not found for deactivation: id={}", command.id().value());
            return Result.failure(new InspectionItemCommandFailure.NotFound());
        }

        var item = existingItem.get();
        if (!item.isActive()) {
            log.warn("Inspection item is already inactive: id={}", command.id().value());
            return Result.failure(new InspectionItemCommandFailure.AlreadyInactive());
        }

        item.deactivate();
        var deactivatedItem = inspectionItemRepository.save(item);
        log.info("Inspection item deactivated successfully: id={}, code={}", deactivatedItem.getId().value(), deactivatedItem.getCode().value());
        return Result.success(deactivatedItem);
    }
}
