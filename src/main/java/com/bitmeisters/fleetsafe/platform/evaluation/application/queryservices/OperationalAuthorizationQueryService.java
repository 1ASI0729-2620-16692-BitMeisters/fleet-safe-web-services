package com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetAuthorizationHistoryByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetCurrentAuthorizationByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetCurrentAuthorizationsQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetOverridesByPeriodQuery;

import java.util.List;
import java.util.Optional;

public interface OperationalAuthorizationQueryService {

    Optional<OperationalAuthorization> handle(GetCurrentAuthorizationByVehicleIdQuery query);

    List<OperationalAuthorization> handle(GetAuthorizationHistoryByVehicleIdQuery query);

    List<OperationalAuthorization> handle(GetOverridesByPeriodQuery query);

    List<OperationalAuthorization> handle(GetCurrentAuthorizationsQuery query);
}
