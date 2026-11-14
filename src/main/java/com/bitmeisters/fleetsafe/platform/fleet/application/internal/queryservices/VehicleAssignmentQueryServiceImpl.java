package com.bitmeisters.fleetsafe.platform.fleet.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.fleet.application.queryservices.ActiveVehicleAssignment;
import com.bitmeisters.fleetsafe.platform.fleet.application.queryservices.VehicleAssignmentQueryService;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.VehicleAssignment;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetActiveVehicleAssignmentOfUserQuery;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetVehicleAssignmentsByCompanyIdQuery;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.DriverRepository;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.VehicleAssignmentRepository;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VehicleAssignmentQueryServiceImpl implements VehicleAssignmentQueryService {

    private final VehicleAssignmentRepository vehicleAssignmentRepository;
    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;

    public VehicleAssignmentQueryServiceImpl(VehicleAssignmentRepository vehicleAssignmentRepository,
                                             DriverRepository driverRepository,
                                             VehicleRepository vehicleRepository) {
        this.vehicleAssignmentRepository = vehicleAssignmentRepository;
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<VehicleAssignment> handle(GetVehicleAssignmentsByCompanyIdQuery query) {
        var results = query.onlyActive()
                ? vehicleAssignmentRepository.findAllActiveByCompanyId(query.companyId())
                : vehicleAssignmentRepository.findAllByCompanyId(query.companyId());
        log.debug("Found {} vehicle assignments for company {}, only active {}",
                results.size(), query.companyId().value(), query.onlyActive());
        return results;
    }

    @Override
    public Optional<ActiveVehicleAssignment> handle(GetActiveVehicleAssignmentOfUserQuery query) {
        var driver = driverRepository.findByUserIdAndCompanyId(query.userId(), query.companyId());
        if (driver.isEmpty()) {
            return Optional.empty();
        }
        var assignment = vehicleAssignmentRepository.findActiveByDriverId(new DriverId(driver.get().getId()));
        if (assignment.isEmpty()) {
            return Optional.empty();
        }
        return vehicleRepository.findByIdAndCompanyId(assignment.get().getVehicleId(), query.companyId())
                .map(vehicle -> new ActiveVehicleAssignment(assignment.get(), vehicle));
    }
}
