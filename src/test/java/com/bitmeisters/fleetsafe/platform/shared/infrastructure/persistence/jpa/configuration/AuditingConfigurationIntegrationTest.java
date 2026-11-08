package com.bitmeisters.fleetsafe.platform.shared.infrastructure.persistence.jpa.configuration;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationRuleCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices.EvaluationRuleQueryService;
import com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices.OperationalAuthorizationQueryService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DefineEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.EvaluateInspectionCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetCurrentAuthorizationByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetEvaluationRuleByIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultSnapshot;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleName;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AuditingConfigurationIntegrationTest {

    @Autowired
    private EvaluationRuleCommandService evaluationRuleCommandService;

    @Autowired
    private EvaluationRuleQueryService evaluationRuleQueryService;

    @Autowired
    private EvaluationCommandService evaluationCommandService;

    @Autowired
    private OperationalAuthorizationQueryService operationalAuthorizationQueryService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void theAuditDatesInMemoryAreTheOnesStoredInTheDatabase() {
        var rule = evaluationRuleCommandService.handle(new DefineEvaluationRuleCommand(
                new InspectionItemId(UUID.randomUUID()), new RuleName("Brakes"), ResultValue.FAIL, RuleImpact.BLOCKING, null))
                .success().orElseThrow();
        entityManager.flush();
        entityManager.clear();

        var stored = evaluationRuleQueryService.handle(new GetEvaluationRuleByIdQuery(rule.getId())).orElseThrow();

        assertThat(stored.getCreatedAt()).isEqualTo(rule.getCreatedAt());
        assertThat(stored.getUpdatedAt()).isEqualTo(rule.getUpdatedAt());
    }

    @Test
    void theDatesSetByTheDomainInMemoryAreTheOnesStoredInTheDatabase() {
        var vehicleId = new VehicleId(UUID.randomUUID());
        var authorization = evaluationCommandService.handle(new EvaluateInspectionCommand(
                new InspectionId(UUID.randomUUID()), vehicleId,
                List.of(new InspectionResultSnapshot(new InspectionResultId(UUID.randomUUID()),
                        new InspectionItemId(UUID.randomUUID()), ResultValue.OK)), null))
                .success().orElseThrow();
        entityManager.flush();
        entityManager.clear();

        var stored = operationalAuthorizationQueryService.handle(new GetCurrentAuthorizationByVehicleIdQuery(vehicleId)).orElseThrow();

        assertThat(stored.getAuthorizedAt()).isEqualTo(authorization.getAuthorizedAt());
    }
}
