package com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.Inspection;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.AddObservationCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.AttachEvidenceCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.CompleteInspectionCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.RegisterInspectionResultCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.StartInspectionCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.UpdateInspectionResultCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

/**
 * InspectionCommandService port interface.
 *
 * <p>
 * Declares command execution operations for the lifecycle of vehicle pre-operational safety inspections.
 * </p>
 */
public interface InspectionCommandService {

    /**
     * Starts a new pre-operational inspection for a vehicle.
     *
     * @param command the start inspection command
     * @return result monad with the created inspection or failure
     */
    Result<Inspection, InspectionCommandFailure> handle(StartInspectionCommand command);

    /**
     * Registers an individual checklist item evaluation result.
     *
     * @param command the register result command
     * @return result monad with the updated inspection or failure
     */
    Result<Inspection, InspectionCommandFailure> handle(RegisterInspectionResultCommand command);

    /**
     * Updates an evaluation result while the inspection is in progress.
     *
     * @param command the update result command
     * @return result monad with the updated inspection or failure
     */
    Result<Inspection, InspectionCommandFailure> handle(UpdateInspectionResultCommand command);

    /**
     * Adds an observation note to a specific inspection result.
     *
     * @param command the add observation command
     * @return result monad with the updated inspection or failure
     */
    Result<Inspection, InspectionCommandFailure> handle(AddObservationCommand command);

    /**
     * Attaches photographic evidence to an observation under an inspection result.
     *
     * @param command the attach evidence command
     * @return result monad with the updated inspection or failure
     */
    Result<Inspection, InspectionCommandFailure> handle(AttachEvidenceCommand command);

    /**
     * Completes the inspection and triggers domain operational evaluations.
     *
     * @param command the complete inspection command
     * @return result monad with the completed inspection or failure
     */
    Result<Inspection, InspectionCommandFailure> handle(CompleteInspectionCommand command);
}
