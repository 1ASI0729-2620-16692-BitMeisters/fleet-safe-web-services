package com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.acl;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.queryservices.InspectionQueryService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.queries.GetInspectionByIdQuery;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.valueobjects.InspectionStatus;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.acl.CompletedInspectionData;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.acl.InspectionResultData;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.acl.InspectionsContextFacade;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class InspectionsContextFacadeImpl implements InspectionsContextFacade {

    private final InspectionQueryService inspectionQueryService;

    public InspectionsContextFacadeImpl(InspectionQueryService inspectionQueryService) {
        this.inspectionQueryService = inspectionQueryService;
    }

    @Override
    public Optional<CompletedInspectionData> fetchCompletedInspection(UUID inspectionId) {
        return inspectionQueryService.handle(new GetInspectionByIdQuery(new InspectionId(inspectionId)))
                .filter(inspection -> inspection.getStatus() == InspectionStatus.COMPLETED)
                .map(inspection -> new CompletedInspectionData(
                        inspection.getId().value(),
                        inspection.getVehicleId().value(),
                        inspection.getResults().stream()
                                .map(result -> new InspectionResultData(result.getId().value(),
                                        result.getInspectionItemId().value(), result.getResult().name()))
                                .toList()));
    }
}
