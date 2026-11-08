package com.bitmeisters.fleetsafe.platform.evaluation.application.internal.outboundservices.acl;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.EvaluateInspectionCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultSnapshot;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.acl.InspectionsContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ExternalInspectionService {

    private final InspectionsContextFacade inspectionsContextFacade;

    public ExternalInspectionService(InspectionsContextFacade inspectionsContextFacade) {
        this.inspectionsContextFacade = inspectionsContextFacade;
    }

    public Optional<EvaluateInspectionCommand> evaluationOf(UUID inspectionId, UUID evaluatedBy) {
        return inspectionsContextFacade.fetchCompletedInspection(inspectionId)
                .map(inspection -> new EvaluateInspectionCommand(
                        new InspectionId(inspection.inspectionId()),
                        new VehicleId(inspection.vehicleId()),
                        inspection.results().stream()
                                .map(result -> new InspectionResultSnapshot(
                                        new InspectionResultId(result.resultId()),
                                        new InspectionItemId(result.inspectionItemId()),
                                        ResultValue.valueOf(result.result())))
                                .toList(),
                        evaluatedBy));
    }
}
