package com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.events;

import java.util.UUID;

public record InspectionCompletedIntegrationEvent(UUID inspectionId, UUID vehicleId, UUID driverId) {
}
