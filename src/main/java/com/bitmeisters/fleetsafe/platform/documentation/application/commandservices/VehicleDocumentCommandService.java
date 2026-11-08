package com.bitmeisters.fleetsafe.platform.documentation.application.commandservices;

import com.bitmeisters.fleetsafe.platform.documentation.domain.model.aggregates.VehicleDocument;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RecalculateDocumentStatusCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.RegisterVehicleDocumentCommand;
import com.bitmeisters.fleetsafe.platform.documentation.domain.model.commands.UpdateVehicleDocumentCommand;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;

public interface VehicleDocumentCommandService {

    Result<VehicleDocument, VehicleDocumentCommandFailure> handle(RegisterVehicleDocumentCommand command);

    Result<VehicleDocument, VehicleDocumentCommandFailure> handle(UpdateVehicleDocumentCommand command);

    int handle(RecalculateDocumentStatusCommand command);
}
