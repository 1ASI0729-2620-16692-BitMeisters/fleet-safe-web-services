package com.bitmeisters.fleetsafe.platform.fleet.domain.model.events;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.FleetId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.Plate;

public record VehicleRegisteredEvent(FleetId fleetId, Plate plate) {
}
