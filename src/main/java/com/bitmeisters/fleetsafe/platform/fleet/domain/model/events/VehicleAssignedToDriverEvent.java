package com.bitmeisters.fleetsafe.platform.fleet.domain.model.events;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleId;

public record VehicleAssignedToDriverEvent(VehicleId vehicleId, DriverId driverId) {
}
