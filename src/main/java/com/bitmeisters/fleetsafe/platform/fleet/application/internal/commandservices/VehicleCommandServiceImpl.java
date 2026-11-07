package com.bitmeisters.fleetsafe.platform.fleet.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.VehicleCommandFailure;
import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.VehicleCommandService;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Vehicle;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.RegisterVehicleCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.UpdateVehicleCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.FleetRepository;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.VehicleRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class VehicleCommandServiceImpl implements VehicleCommandService {

    private final VehicleRepository vehicleRepository;
    private final FleetRepository fleetRepository;

    public VehicleCommandServiceImpl(VehicleRepository vehicleRepository, FleetRepository fleetRepository) {
        this.vehicleRepository = vehicleRepository;
        this.fleetRepository = fleetRepository;
    }

    @Override
    @Transactional
    public Result<Vehicle, VehicleCommandFailure> handle(RegisterVehicleCommand command) {
        if (!fleetRepository.existsByIdAndCompanyId(command.fleetId(), command.companyId())) {
            log.warn("Fleet not found while registering a vehicle: fleetId={}, companyId={}",
                    command.fleetId().value(), command.companyId().value());
            return Result.failure(new VehicleCommandFailure.FleetNotFound());
        }
        if (vehicleRepository.existsByPlate(command.plate())) {
            log.warn("Duplicate plate: plate={}", command.plate().value());
            return Result.failure(new VehicleCommandFailure.DuplicatePlate());
        }
        var vehicle = vehicleRepository.save(new Vehicle(command));
        log.info("Vehicle registered: id={}, plate={}, fleetId={}",
                vehicle.getId(), command.plate().value(), command.fleetId().value());
        return Result.success(vehicle);
    }

    @Override
    @Transactional
    public Result<Vehicle, VehicleCommandFailure> handle(UpdateVehicleCommand command) {
        var vehicle = vehicleRepository.findByIdAndCompanyId(command.vehicleId(), command.companyId());
        if (vehicle.isEmpty()) {
            log.warn("Vehicle not found: id={}, companyId={}", command.vehicleId().value(), command.companyId().value());
            return Result.failure(new VehicleCommandFailure.NotFound());
        }
        vehicle.get().update(command);
        var updatedVehicle = vehicleRepository.save(vehicle.get());
        log.info("Vehicle updated: id={}", updatedVehicle.getId());
        return Result.success(updatedVehicle);
    }
}
