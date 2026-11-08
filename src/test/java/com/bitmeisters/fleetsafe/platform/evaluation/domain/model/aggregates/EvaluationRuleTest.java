package com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates;

import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DefineEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EvaluationRuleTest {

    private final InspectionItemId brakeSystem = new InspectionItemId(UUID.randomUUID());

    private EvaluationRule brakeFailureRule() {
        return new EvaluationRule(new DefineEvaluationRuleCommand(
                brakeSystem,
                new RuleName("Brake system failure blocks the vehicle"),
                ResultValue.FAIL,
                RuleImpact.BLOCKING,
                null));
    }

    @Test
    void isActiveWhenDefined() {
        assertThat(brakeFailureRule().isActive()).isTrue();
    }

    @Test
    void appliesToItsItemAndResult() {
        assertThat(brakeFailureRule().appliesTo(brakeSystem, ResultValue.FAIL)).isTrue();
    }

    @Test
    void doesNotApplyToAnotherResultOfTheSameItem() {
        assertThat(brakeFailureRule().appliesTo(brakeSystem, ResultValue.OBSERVED)).isFalse();
    }

    @Test
    void doesNotApplyToAnotherItem() {
        assertThat(brakeFailureRule().appliesTo(new InspectionItemId(UUID.randomUUID()), ResultValue.FAIL)).isFalse();
    }

    @Test
    void stopsApplyingOnceDeactivated() {
        var rule = brakeFailureRule();

        rule.deactivate();

        assertThat(rule.isActive()).isFalse();
        assertThat(rule.appliesTo(brakeSystem, ResultValue.FAIL)).isFalse();
    }

    @Test
    void rejectsABlankName() {
        assertThatThrownBy(() -> new RuleName(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("evaluation.rule.error.name.required");
    }

    @Test
    void rejectsANameLongerThanItsLimit() {
        assertThatThrownBy(() -> new RuleName("x".repeat(RuleName.MAX_LENGTH + 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("evaluation.rule.error.name.size");
    }

    @Test
    void rejectsACommandWithoutImpact() {
        assertThatThrownBy(() -> new DefineEvaluationRuleCommand(
                brakeSystem, new RuleName("Brake rule"), ResultValue.FAIL, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("evaluation.rule.error.resulting-impact.required");
    }
}
