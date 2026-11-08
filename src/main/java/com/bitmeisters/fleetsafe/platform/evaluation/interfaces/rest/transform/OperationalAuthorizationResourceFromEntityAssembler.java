package com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.transform;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.evaluation.interfaces.rest.resources.OperationalAuthorizationResource;

public class OperationalAuthorizationResourceFromEntityAssembler {

    public static OperationalAuthorizationResource toResourceFromEntity(OperationalAuthorization entity) {
        return new OperationalAuthorizationResource(
                entity.getId(),
                entity.getEvaluationId(),
                entity.getVehicleId().value(),
                entity.getStatus().name(),
                entity.isOverride(),
                entity.getOverrideReason(),
                entity.getAuthorizedBy(),
                entity.getAuthorizedAt(),
                entity.getValidUntil());
    }
}
