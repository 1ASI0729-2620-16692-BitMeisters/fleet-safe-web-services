package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DefineEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.EvaluateInspectionCommand;
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

class EvaluationTest {

    private final InspectionItemId brakes = new InspectionItemId(UUID.randomUUID());
    private final InspectionItemId lights = new InspectionItemId(UUID.randomUUID());

    private final List<EvaluationRule> activeRules = List.of(
            rule(brakes, ResultValue.FAIL, RuleImpact.BLOCKING),
            rule(lights, ResultValue.OBSERVED, RuleImpact.OBSERVED));

    private static EvaluationRule rule(InspectionItemId item, ResultValue value, RuleImpact impact) {
        return new EvaluationRule(new DefineEvaluationRuleCommand(item, new RuleName("Rule"), value, impact, null));
    }

    private static InspectionResultSnapshot result(InspectionItemId item, ResultValue value) {
        return new InspectionResultSnapshot(new InspectionResultId(UUID.randomUUID()), item, value);
    }

    private Evaluation evaluate(InspectionResultSnapshot... results) {
        return new Evaluation(new EvaluateInspectionCommand(
                new InspectionId(UUID.randomUUID()), new VehicleId(UUID.randomUUID()), List.of(results), null), activeRules);
    }

    @Test
    void recordsADetailForEachResultWithAMatchingRule() {
        var evaluation = evaluate(result(brakes, ResultValue.FAIL), result(lights, ResultValue.OBSERVED), result(lights, ResultValue.OK));

        assertThat(evaluation.getDetails()).hasSize(2);
    }

    @Test
    void aSingleBlockingImpactMakesTheVehicleNotEnabled() {
        var evaluation = evaluate(result(lights, ResultValue.OBSERVED), result(brakes, ResultValue.FAIL), result(lights, ResultValue.OK));

        assertThat(evaluation.resolveMostRestrictiveImpact()).isEqualTo(RuleImpact.BLOCKING);
        assertThat(AuthorizationStatus.fromImpact(evaluation.resolveMostRestrictiveImpact())).isEqualTo(AuthorizationStatus.NOT_ENABLED);
    }

    @Test
    void anObservedImpactWithoutBlockingLeavesTheVehicleObserved() {
        var evaluation = evaluate(result(lights, ResultValue.OBSERVED), result(brakes, ResultValue.OK));

        assertThat(AuthorizationStatus.fromImpact(evaluation.resolveMostRestrictiveImpact())).isEqualTo(AuthorizationStatus.OBSERVED);
    }

    @Test
    void withoutMatchingRulesTheVehicleIsEnabled() {
        var evaluation = evaluate(result(brakes, ResultValue.OK), result(lights, ResultValue.OK));

        assertThat(evaluation.getDetails()).isEmpty();
        assertThat(AuthorizationStatus.fromImpact(evaluation.resolveMostRestrictiveImpact())).isEqualTo(AuthorizationStatus.ENABLED);
    }

    @Test
    void ignoresInactiveRules() {
        activeRules.getFirst().deactivate();

        var evaluation = evaluate(result(brakes, ResultValue.FAIL));

        assertThat(evaluation.resolveMostRestrictiveImpact()).isEqualTo(RuleImpact.NONE);
    }

    @Test
    void isAutomaticWhenNobodyEvaluated() {
        assertThat(evaluate(result(brakes, ResultValue.OK)).isAutomatic()).isTrue();
    }
}
