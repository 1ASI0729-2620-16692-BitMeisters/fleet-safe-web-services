package com.bitmeisters.fleetsafe.platform.fleet.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.fleet.application.queryservices.DriverQueryService;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Driver;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetDriversByCompanyIdQuery;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.DriverRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DriverQueryServiceImpl implements DriverQueryService {

    private final DriverRepository driverRepository;

    public DriverQueryServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public List<Driver> handle(GetDriversByCompanyIdQuery query) {
        var results = driverRepository.findAllByCompanyId(query.companyId());
        log.debug("Found {} drivers for company {}", results.size(), query.companyId().value());
        return results;
    }
}
