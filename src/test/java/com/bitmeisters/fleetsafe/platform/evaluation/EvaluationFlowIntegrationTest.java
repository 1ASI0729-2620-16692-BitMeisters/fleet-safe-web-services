package com.bitmeisters.fleetsafe.platform.evaluation;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationCommandFailure;
import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationRuleCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.OperationalAuthorizationCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices.EvaluationQueryService;
import com.bitmeisters.fleetsafe.platform.evaluation.application.queryservices.OperationalAuthorizationQueryService;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DefineEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.EvaluateInspectionCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.LiftBlockCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetAuthorizationHistoryByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetCurrentAuthorizationByVehicleIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetEvaluationByInspectionIdQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.queries.GetOverridesByPeriodQuery;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.events.InspectionEvaluatedEvent;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.AuthorizationStatus;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultSnapshot;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleName;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RecordApplicationEvents
class EvaluationFlowIntegrationTest {

    @Autowired
    private ApplicationEvents applicationEvents;


    @Autowired
    private EvaluationRuleCommandService evaluationRuleCommandService;

    @Autowired
    private EvaluationCommandService evaluationCommandService;

    @Autowired
    private EvaluationQueryService evaluationQueryService;

    @Autowired
    private OperationalAuthorizationQueryService operationalAuthorizationQueryService;

    @Autowired
    private OperationalAuthorizationCommandService operationalAuthorizationCommandService;

    @Test
    void evaluatesAnInspectionAndKeepsTheVehicleStatus() {
        var brakes = new InspectionItemId(UUID.randomUUID());
        evaluationRuleCommandService.handle(new DefineEvaluationRuleCommand(
                brakes, new RuleName("Brake failure blocks the vehicle"), ResultValue.FAIL, RuleImpact.BLOCKING, null));
        var inspectionId = new InspectionId(UUID.randomUUID());
        var vehicleId = new VehicleId(UUID.randomUUID());
        var command = new EvaluateInspectionCommand(inspectionId, vehicleId, List.of(
                new InspectionResultSnapshot(new InspectionResultId(UUID.randomUUID()), brakes, ResultValue.FAIL),
                new InspectionResultSnapshot(new InspectionResultId(UUID.randomUUID()), new InspectionItemId(UUID.randomUUID()), ResultValue.OK)),
                null);

        var authorization = evaluationCommandService.handle(command);

        assertThat(authorization.success()).get().extracting(OperationalAuthorization::getStatus).isEqualTo(AuthorizationStatus.NOT_ENABLED);
        assertThat(applicationEvents.stream(InspectionEvaluatedEvent.class))
                .containsExactly(new InspectionEvaluatedEvent(inspectionId, vehicleId, AuthorizationStatus.NOT_ENABLED));
        assertThat(evaluationQueryService.handle(new GetEvaluationByInspectionIdQuery(inspectionId)))
                .get().satisfies(evaluation -> assertThat(evaluation.getDetails()).hasSize(1));
        assertThat(operationalAuthorizationQueryService.handle(new GetCurrentAuthorizationByVehicleIdQuery(vehicleId)))
                .get().extracting(OperationalAuthorization::getStatus).isEqualTo(AuthorizationStatus.NOT_ENABLED);
        assertThat(operationalAuthorizationQueryService.handle(new GetAuthorizationHistoryByVehicleIdQuery(vehicleId))).hasSize(1);
        assertThat(evaluationCommandService.handle(command).failure())
                .get().isInstanceOf(EvaluationCommandFailure.AlreadyEvaluated.class);

        var lifted = operationalAuthorizationCommandService.handle(new LiftBlockCommand(vehicleId, "Urgent delivery", UUID.randomUUID()));

        assertThat(lifted.success()).get().extracting(OperationalAuthorization::getStatus).isEqualTo(AuthorizationStatus.ENABLED);
        assertThat(operationalAuthorizationQueryService.handle(new GetCurrentAuthorizationByVehicleIdQuery(vehicleId)))
                .get().extracting(OperationalAuthorization::isOverride).isEqualTo(true);
        assertThat(operationalAuthorizationQueryService.handle(new GetAuthorizationHistoryByVehicleIdQuery(vehicleId))).hasSize(2);
        var now = Instant.now();
        assertThat(operationalAuthorizationQueryService.handle(new GetOverridesByPeriodQuery(now.minus(1, ChronoUnit.HOURS), now)))
                .extracting(OperationalAuthorization::getVehicleId).contains(vehicleId);
    }
}
