package com.bitmeisters.fleetsafe.platform.evaluation.infrastructure.persistence.jpa.assemblers;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.EvaluationRule;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationRulePersistenceAssemblerTest {

    @Test
    void keepsEveryFieldWhenGoingToTheDatabaseAndBack() {
        var created = Instant.parse("2026-10-26T14:00:00Z");
        var rule = EvaluationRule.restore(UUID.randomUUID(), new InspectionItemId(UUID.randomUUID()),
                new RuleName("Brake failure blocks the vehicle"), ResultValue.FAIL, RuleImpact.BLOCKING,
                "Unsafe to operate", false, created, created);

        var restored = EvaluationRulePersistenceAssembler.toDomainFromPersistence(
                EvaluationRulePersistenceAssembler.toPersistenceFromDomain(rule));

        assertThat(restored).usingRecursiveComparison().ignoringFields("updatedAt").isEqualTo(rule);
    }
}
