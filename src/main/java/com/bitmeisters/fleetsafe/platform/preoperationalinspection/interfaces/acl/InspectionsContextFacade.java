package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.acl;

import java.util.Optional;
import java.util.UUID;

public interface InspectionsContextFacade {

    Optional<CompletedInspectionData> fetchCompletedInspection(UUID inspectionId);
}
