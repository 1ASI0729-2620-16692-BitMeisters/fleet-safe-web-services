package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.acl;

import java.util.List;
import java.util.UUID;

public record CompletedInspectionData(UUID inspectionId, UUID vehicleId, List<InspectionResultData> results) {
}
