package com.bitmeisters.fleetsafe.platform.fleet.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.fleet.application.queryservices.FleetQueryService;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.aggregates.Fleet;
import com.bitmeisters.fleetsafe.platform.fleet.domain.model.queries.GetFleetsByCompanyIdQuery;
import com.bitmeisters.fleetsafe.platform.fleet.domain.repositories.FleetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FleetQueryServiceImpl implements FleetQueryService {

    private final FleetRepository fleetRepository;

    public FleetQueryServiceImpl(FleetRepository fleetRepository) {
        this.fleetRepository = fleetRepository;
    }

    @Override
    public List<Fleet> handle(GetFleetsByCompanyIdQuery query) {
        var results = fleetRepository.findAllByCompanyId(query.companyId());
        log.debug("Found {} fleets for company {}", results.size(), query.companyId().value());
        return results;
    }
}
