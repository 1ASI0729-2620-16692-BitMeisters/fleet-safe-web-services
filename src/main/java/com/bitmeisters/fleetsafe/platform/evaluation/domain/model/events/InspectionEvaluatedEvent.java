package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.events;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.AuthorizationStatus;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;

public record InspectionEvaluatedEvent(InspectionId inspectionId, VehicleId vehicleId, AuthorizationStatus status) {
}
