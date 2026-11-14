package com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.events.VehicleAssignedToDriverEvent;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.events.VehicleAssignmentClosedEvent;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * The period in which a vehicle is handed to a driver. The driver inspects the vehicle he has
 * assigned, so a vehicle has at most one active assignment and so does a driver. A closed
 * assignment is kept as history.
 */
@Getter
public class VehicleAssignment extends AbstractDomainAggregateRoot<VehicleAssignment> {

    private UUID id;
    private VehicleId vehicleId;
    private DriverId driverId;
    private LocalDate assignedFrom;
    private LocalDate assignedTo;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;

    private VehicleAssignment() {
    }

    /**
     * Starts an assignment that stays in force from the given day until it is closed.
     */
    public static VehicleAssignment start(VehicleId vehicleId, DriverId driverId, LocalDate from) {
        var assignment = new VehicleAssignment();
        assignment.vehicleId = vehicleId;
        assignment.driverId = driverId;
        assignment.assignedFrom = from;
        assignment.assignedTo = null;
        assignment.active = true;
        assignment.registerDomainEvent(new VehicleAssignedToDriverEvent(vehicleId, driverId));
        return assignment;
    }

    public static VehicleAssignment restore(UUID id, VehicleId vehicleId, DriverId driverId, LocalDate assignedFrom,
                                            LocalDate assignedTo, boolean active, Instant createdAt, Instant updatedAt) {
        var assignment = new VehicleAssignment();
        assignment.id = id;
        assignment.vehicleId = vehicleId;
        assignment.driverId = driverId;
        assignment.assignedFrom = assignedFrom;
        assignment.assignedTo = assignedTo;
        assignment.active = active;
        assignment.createdAt = createdAt;
        assignment.updatedAt = updatedAt;
        return assignment;
    }

    /**
     * Ends the assignment on the given day. It is not allowed to end before it started.
     */
    public void close(LocalDate endDate) {
        this.assignedTo = endDate.isBefore(assignedFrom) ? assignedFrom : endDate;
        this.active = false;
        registerDomainEvent(new VehicleAssignmentClosedEvent(vehicleId, driverId));
    }
}
