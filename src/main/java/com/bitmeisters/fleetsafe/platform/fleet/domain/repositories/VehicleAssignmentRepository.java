package com.bitmeisters.fleetsafe.platform.fleet.domain.repositories;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.VehicleAssignment;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.DriverId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleAssignmentId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleId;

import java.util.List;
import java.util.Optional;

/**
 * An assignment belongs to a company through its vehicle, so the queries by company look the vehicle up.
 */
public interface VehicleAssignmentRepository {

    Optional<VehicleAssignment> findByIdAndCompanyId(VehicleAssignmentId id, CompanyId companyId);

    List<VehicleAssignment> findAllByCompanyId(CompanyId companyId);

    List<VehicleAssignment> findAllActiveByCompanyId(CompanyId companyId);

    Optional<VehicleAssignment> findActiveByDriverId(DriverId driverId);

    boolean existsActiveByVehicleId(VehicleId vehicleId);

    boolean existsActiveByDriverId(DriverId driverId);

    VehicleAssignment save(VehicleAssignment assignment);
}
