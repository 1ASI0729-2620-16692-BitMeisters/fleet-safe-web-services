package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.Evaluation;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.entities.EvaluationDetail;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.AuthorizationStatus;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationPersistenceAssemblerTest {

    private final Instant created = Instant.parse("2026-10-26T14:00:00Z");

    @Test
    void keepsTheEvaluationAndItsDetailsWhenGoingToTheDatabaseAndBack() {
        var evaluation = Evaluation.restore(UUID.randomUUID(), new InspectionId(UUID.randomUUID()), created, null,
                List.of(new EvaluationDetail(UUID.randomUUID(), new InspectionResultId(UUID.randomUUID()), UUID.randomUUID(), RuleImpact.BLOCKING),
                        new EvaluationDetail(UUID.randomUUID(), new InspectionResultId(UUID.randomUUID()), UUID.randomUUID(), RuleImpact.OBSERVED)),
                created, created);

        var entity = EvaluationPersistenceAssembler.toPersistenceFromDomain(evaluation);
        var restored = EvaluationPersistenceAssembler.toDomainFromPersistence(entity);

        assertThat(entity.getDetails()).allSatisfy(detail -> assertThat(detail.getEvaluation()).isSameAs(entity));
        assertThat(restored).usingRecursiveComparison().ignoringFields("updatedAt").isEqualTo(evaluation);
        assertThat(restored.resolveMostRestrictiveImpact()).isEqualTo(RuleImpact.BLOCKING);
    }

    @Test
    void keepsEveryFieldOfAnAuthorizationWhenGoingToTheDatabaseAndBack() {
        var authorization = OperationalAuthorization.restore(UUID.randomUUID(), UUID.randomUUID(), new VehicleId(UUID.randomUUID()),
                AuthorizationStatus.ENABLED, true, "Urgent delivery", UUID.randomUUID(), created, null, created, created);

        var restored = OperationalAuthorizationPersistenceAssembler.toDomainFromPersistence(
                OperationalAuthorizationPersistenceAssembler.toPersistenceFromDomain(authorization));

        assertThat(restored).usingRecursiveComparison().ignoringFields("updatedAt").isEqualTo(authorization);
    }
}
