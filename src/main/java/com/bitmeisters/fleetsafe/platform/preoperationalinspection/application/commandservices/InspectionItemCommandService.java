package com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.commandservices;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.aggregates.InspectionItem;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.CreateInspectionItemCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.DeactivateInspectionItemCommand;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.commands.UpdateInspectionItemCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

/**
 * InspectionItemCommandService port interface.
 *
 * <p>
 * Declares command execution contracts for the checklist inspection items catalog.
 * </p>
 */
public interface InspectionItemCommandService {

    /**
     * Handles the creation of a new inspection item in the catalog.
     *
     * @param command the creation command
     * @return result monad with the created item or failure
     */
    Result<InspectionItem, InspectionItemCommandFailure> handle(CreateInspectionItemCommand command);

    /**
     * Handles updating an existing inspection item.
     *
     * @param command the update command
     * @return result monad with the updated item or failure
     */
    Result<InspectionItem, InspectionItemCommandFailure> handle(UpdateInspectionItemCommand command);

    /**
     * Handles deactivating an inspection item from the catalog.
     *
     * @param command the deactivation command
     * @return result monad with the deactivated item or failure
     */
    Result<InspectionItem, InspectionItemCommandFailure> handle(DeactivateInspectionItemCommand command);
}
