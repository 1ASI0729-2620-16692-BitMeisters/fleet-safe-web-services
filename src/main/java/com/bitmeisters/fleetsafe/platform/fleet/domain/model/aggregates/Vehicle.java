package com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.RegisterVehicleCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.UpdateVehicleCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.events.VehicleRegisteredEvent;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.FleetId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.LoadCapacity;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.ManufacturingYear;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.Plate;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleBrand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleModel;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleStatus;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleType;
import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * A vehicle of a fleet.
 * Its status is derived from the latest operational authorization, so it is never provided when the
 * vehicle is registered: every vehicle starts enabled.
 */
@Getter
public class Vehicle extends AbstractDomainAggregateRoot<Vehicle> {

    public static final VehicleStatus INITIAL_STATUS = VehicleStatus.ENABLED;

    private UUID id;
    private FleetId fleetId;
    private Plate plate;
    private VehicleBrand brand;
    private VehicleModel model;
    private ManufacturingYear year;
    private VehicleType type;
    private LoadCapacity capacity;
    private VehicleStatus currentStatus;
    private Instant createdAt;
    private Instant updatedAt;

    private Vehicle() {
    }

    public Vehicle(RegisterVehicleCommand command) {
        this.fleetId = command.fleetId();
        this.plate = command.plate();
        this.brand = command.brand();
        this.model = command.model();
        this.year = command.year();
        this.type = command.type();
        this.capacity = command.capacity();
        this.currentStatus = INITIAL_STATUS;
        registerDomainEvent(new VehicleRegisteredEvent(command.fleetId(), command.plate()));
    }

    public static Vehicle restore(UUID id, FleetId fleetId, Plate plate, VehicleBrand brand, VehicleModel model,
                                  ManufacturingYear year, VehicleType type, LoadCapacity capacity,
                                  VehicleStatus currentStatus, Instant createdAt, Instant updatedAt) {
        var vehicle = new Vehicle();
        vehicle.id = id;
        vehicle.fleetId = fleetId;
        vehicle.plate = plate;
        vehicle.brand = brand;
        vehicle.model = model;
        vehicle.year = year;
        vehicle.type = type;
        vehicle.capacity = capacity;
        vehicle.currentStatus = currentStatus;
        vehicle.createdAt = createdAt;
        vehicle.updatedAt = updatedAt;
        return vehicle;
    }

    public void update(UpdateVehicleCommand command) {
        this.brand = command.brand();
        this.model = command.model();
        this.year = command.year();
        this.type = command.type();
        this.capacity = command.capacity();
    }
}
