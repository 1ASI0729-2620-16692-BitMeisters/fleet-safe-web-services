package com.bitmeisters.fleetsafe.platform.evaluation.application.internal.queryservices;

import com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices.OperationalAuthorizationQueryService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetAuthorizationHistoryByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetCurrentAuthorizationByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetCurrentAuthorizationsQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetOverridesByPeriodQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.OperationalAuthorizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OperationalAuthorizationQueryServiceImpl implements OperationalAuthorizationQueryService {

    private final OperationalAuthorizationRepository operationalAuthorizationRepository;

    public OperationalAuthorizationQueryServiceImpl(OperationalAuthorizationRepository operationalAuthorizationRepository) {
        this.operationalAuthorizationRepository = operationalAuthorizationRepository;
    }

    @Override
    public Optional<OperationalAuthorization> handle(GetCurrentAuthorizationByVehicleIdQuery query) {
        var result = operationalAuthorizationRepository.findFirstByVehicleIdOrderByAuthorizedAtDesc(query.vehicleId());
        if (result.isEmpty()) log.debug("No authorization found for vehicleId: {}", query.vehicleId().value());
        return result;
    }

    @Override
    public List<OperationalAuthorization> handle(GetAuthorizationHistoryByVehicleIdQuery query) {
        var results = operationalAuthorizationRepository.findAllByVehicleIdOrderByAuthorizedAtDesc(query.vehicleId());
        log.debug("Found {} authorizations for vehicleId: {}", results.size(), query.vehicleId().value());
        return results;
    }

    @Override
    public List<OperationalAuthorization> handle(GetOverridesByPeriodQuery query) {
        var results = operationalAuthorizationRepository
                .findAllByOverrideTrueAndAuthorizedAtBetweenOrderByAuthorizedAtDesc(query.from(), query.to());
        log.debug("Found {} overrides between {} and {}", results.size(), query.from(), query.to());
        return results;
    }

    @Override
    public List<OperationalAuthorization> handle(GetCurrentAuthorizationsQuery query) {
        var results = operationalAuthorizationRepository.findLatestOfEachVehicle().stream()
                .filter(authorization -> query.status().map(status -> status == authorization.getStatus()).orElse(true))
                .toList();
        log.debug("Found {} current authorizations with status {}", results.size(), query.status().orElse(null));
        return results;
    }
}
