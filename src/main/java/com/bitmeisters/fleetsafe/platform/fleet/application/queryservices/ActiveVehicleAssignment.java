package com.bitmeisters.fleetsafe.platform.fleet.application.queryservices;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Vehicle;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.VehicleAssignment;

/**
 * An active assignment together with the vehicle it hands to the driver.
 */
public record ActiveVehicleAssignment(VehicleAssignment assignment, Vehicle vehicle) {
}
