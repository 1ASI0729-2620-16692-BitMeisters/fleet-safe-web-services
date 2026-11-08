package com.bitmeisters.fleetsafe.platform.fleet.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.fleet.application.queryservices.VehicleQueryService;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Vehicle;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetVehicleByIdQuery;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetVehiclesByCompanyIdQuery;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class VehicleQueryServiceImpl implements VehicleQueryService {

    private final VehicleRepository vehicleRepository;

    public VehicleQueryServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<Vehicle> handle(GetVehiclesByCompanyIdQuery query) {
        var results = query.status() == null
                ? vehicleRepository.findAllByCompanyId(query.companyId())
                : vehicleRepository.findAllByCompanyIdAndStatus(query.companyId(), query.status());
        log.debug("Found {} vehicles for company {}, status filter {}", results.size(), query.companyId().value(), query.status());
        return results;
    }

    @Override
    public Optional<Vehicle> handle(GetVehicleByIdQuery query) {
        return vehicleRepository.findByIdAndCompanyId(query.vehicleId(), query.companyId());
    }
}
