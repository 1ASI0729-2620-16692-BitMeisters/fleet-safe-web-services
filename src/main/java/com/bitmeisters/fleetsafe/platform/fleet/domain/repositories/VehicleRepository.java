package com.bitmeisters.fleetsafe.platform.fleet.domain.repositories;

import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Vehicle;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.CompanyId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.Plate;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.valueobjects.VehicleStatus;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository {

    Optional<Vehicle> findByIdAndCompanyId(VehicleId id, CompanyId companyId);

    List<Vehicle> findAllByCompanyId(CompanyId companyId);

    List<Vehicle> findAllByCompanyIdAndStatus(CompanyId companyId, VehicleStatus status);

    boolean existsByPlate(Plate plate);

    Vehicle save(Vehicle vehicle);
}
