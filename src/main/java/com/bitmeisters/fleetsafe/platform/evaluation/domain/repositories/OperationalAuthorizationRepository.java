package com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OperationalAuthorizationRepository {

    Optional<OperationalAuthorization> findFirstByVehicleIdOrderByAuthorizedAtDesc(VehicleId vehicleId);

    List<OperationalAuthorization> findAllByVehicleIdOrderByAuthorizedAtDesc(VehicleId vehicleId);

    List<OperationalAuthorization> findAllByOverrideTrueAndAuthorizedAtBetweenOrderByAuthorizedAtDesc(Instant from, Instant to);

    List<OperationalAuthorization> findLatestOfEachVehicle();

    OperationalAuthorization save(OperationalAuthorization authorization);
}
