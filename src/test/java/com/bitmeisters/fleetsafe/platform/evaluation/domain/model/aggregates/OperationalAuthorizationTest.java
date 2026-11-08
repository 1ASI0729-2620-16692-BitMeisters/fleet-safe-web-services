package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DefineEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.EvaluateInspectionCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.LiftBlockCommand;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OperationalAuthorizationTest {

    private final InspectionItemId brakes = new InspectionItemId(UUID.randomUUID());
    private final VehicleId truck = new VehicleId(UUID.randomUUID());
    private final UUID supervisor = UUID.randomUUID();

    private OperationalAuthorization authorizationFor(ResultValue brakesResult) {
        var rule = new EvaluationRule(new DefineEvaluationRuleCommand(brakes, new RuleName("Brakes"), ResultValue.FAIL, RuleImpact.BLOCKING, null));
        var evaluation = new Evaluation(new EvaluateInspectionCommand(new InspectionId(UUID.randomUUID()), truck,
                List.of(new InspectionResultSnapshot(new InspectionResultId(UUID.randomUUID()), brakes, brakesResult)), null), List.of(rule));
        return new OperationalAuthorization(evaluation, truck);
    }

    @Test
    void liftingABlockEnablesTheVehicleAndKeepsWhoAndWhy() {
        var blocked = authorizationFor(ResultValue.FAIL);

        var lifted = new OperationalAuthorization(blocked, new LiftBlockCommand(truck, "Urgent delivery", supervisor));

        assertThat(lifted.getStatus()).isEqualTo(AuthorizationStatus.ENABLED);
        assertThat(lifted.liftsBlock()).isTrue();
        assertThat(lifted.getOverrideReason()).isEqualTo("Urgent delivery");
        assertThat(lifted.getAuthorizedBy()).isEqualTo(supervisor);
        assertThat(lifted.getVehicleId()).isEqualTo(truck);
    }

    @Test
    void cannotLiftTheBlockOfAnEnabledVehicle() {
        var enabled = authorizationFor(ResultValue.OK);

        assertThatThrownBy(() -> new OperationalAuthorization(enabled, new LiftBlockCommand(truck, "No reason", supervisor)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("evaluation.authorization.error.not-blocked");
    }

    @Test
    void requiresAReason() {
        assertThatThrownBy(() -> new LiftBlockCommand(truck, "  ", supervisor))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("evaluation.authorization.error.reason.required");
    }

    @Test
    void requiresTheUserWhoAuthorizes() {
        assertThatThrownBy(() -> new LiftBlockCommand(truck, "Urgent delivery", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("evaluation.authorization.error.authorized-by.required");
    }
}
