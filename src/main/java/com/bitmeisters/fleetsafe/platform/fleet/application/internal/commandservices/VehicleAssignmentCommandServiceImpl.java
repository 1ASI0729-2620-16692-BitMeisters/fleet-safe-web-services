package com.bitmeisters.fleetsafe.platform.fleet.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.VehicleAssignmentCommandFailure;
import com.bitmeisters.fleetsafe.platform.fleet.application.commandservices.VehicleAssignmentCommandService;
import com.bitmeisters.fleetsafe.platform.fleet.application.internal.outboundservices.calendar.FleetCalendar;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.VehicleAssignment;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.AssignVehicleToDriverCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.commands.CloseVehicleAssignmentCommand;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleStatus;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.DriverRepository;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.VehicleAssignmentRepository;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.VehicleRepository;
import com.bitmeisters.fleetsafe.platform.shared.application.patterns.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class VehicleAssignmentCommandServiceImpl implements VehicleAssignmentCommandService {

    private final VehicleAssignmentRepository vehicleAssignmentRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final FleetCalendar fleetCalendar;

    public VehicleAssignmentCommandServiceImpl(VehicleAssignmentRepository vehicleAssignmentRepository,
                                               VehicleRepository vehicleRepository,
                                               DriverRepository driverRepository,
                                               FleetCalendar fleetCalendar) {
        this.vehicleAssignmentRepository = vehicleAssignmentRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
        this.fleetCalendar = fleetCalendar;
    }

    /**
     * The rules are checked in order and the first one broken is reported: the vehicle and the driver
     * must exist in the company, the vehicle must be enabled, the license of the driver must be in
     * force, and neither of them can be committed to another active assignment.
     */
    @Override
    @Transactional
    public Result<VehicleAssignment, VehicleAssignmentCommandFailure> handle(AssignVehicleToDriverCommand command) {
        var vehicle = vehicleRepository.findByIdAndCompanyId(command.vehicleId(), command.companyId());
        if (vehicle.isEmpty()) {
            log.warn("Vehicle not found while assigning it: vehicleId={}, companyId={}",
                    command.vehicleId().value(), command.companyId().value());
            return Result.failure(new VehicleAssignmentCommandFailure.VehicleNotFound());
        }
        var driver = driverRepository.findByIdAndCompanyId(command.driverId(), command.companyId());
        if (driver.isEmpty()) {
            log.warn("Driver not found while assigning a vehicle: driverId={}, companyId={}",
                    command.driverId().value(), command.companyId().value());
            return Result.failure(new VehicleAssignmentCommandFailure.DriverNotFound());
        }
        if (vehicle.get().getCurrentStatus() != VehicleStatus.ENABLED) {
            log.warn("Vehicle is not enabled: vehicleId={}, status={}",
                    command.vehicleId().value(), vehicle.get().getCurrentStatus());
            return Result.failure(new VehicleAssignmentCommandFailure.VehicleNotEnabled());
        }
        if (vehicleAssignmentRepository.existsActiveByVehicleId(command.vehicleId())) {
            log.warn("Vehicle already assigned: vehicleId={}", command.vehicleId().value());
            return Result.failure(new VehicleAssignmentCommandFailure.VehicleAlreadyAssigned());
        }
        var today = fleetCalendar.today();
        if (!driver.get().hasValidLicenseOn(today)) {
            log.warn("License of the driver expired: driverId={}", command.driverId().value());
            return Result.failure(new VehicleAssignmentCommandFailure.DriverLicenseExpired());
        }
        if (vehicleAssignmentRepository.existsActiveByDriverId(command.driverId())) {
            log.warn("Driver already assigned: driverId={}", command.driverId().value());
            return Result.failure(new VehicleAssignmentCommandFailure.DriverAlreadyAssigned());
        }
        var assignment = vehicleAssignmentRepository.save(
                VehicleAssignment.start(command.vehicleId(), command.driverId(), today));
        log.info("Vehicle assigned: id={}, vehicleId={}, driverId={}",
                assignment.getId(), command.vehicleId().value(), command.driverId().value());
        return Result.success(assignment);
    }

    @Override
    @Transactional
    public Result<VehicleAssignment, VehicleAssignmentCommandFailure> handle(CloseVehicleAssignmentCommand command) {
        var assignment = vehicleAssignmentRepository.findByIdAndCompanyId(command.assignmentId(), command.companyId());
        if (assignment.isEmpty()) {
            log.warn("Assignment not found: id={}, companyId={}",
                    command.assignmentId().value(), command.companyId().value());
            return Result.failure(new VehicleAssignmentCommandFailure.NotFound());
        }
        if (!assignment.get().isActive()) {
            log.warn("Assignment already closed: id={}", command.assignmentId().value());
            return Result.failure(new VehicleAssignmentCommandFailure.AlreadyClosed());
        }
        assignment.get().close(fleetCalendar.today());
        var closed = vehicleAssignmentRepository.save(assignment.get());
        log.info("Vehicle assignment closed: id={}", closed.getId());
        return Result.success(closed);
    }
}
