package com.bitmeisters.fleetsafe.platform.evaluation.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationRuleCommandFailure;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.EvaluationRule;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DeactivateEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.DefineEvaluationRuleCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleName;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.EvaluationRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationRuleCommandServiceImplTest {

    @Mock
    private EvaluationRuleRepository evaluationRuleRepository;

    @InjectMocks
    private EvaluationRuleCommandServiceImpl evaluationRuleCommandService;

    private final DefineEvaluationRuleCommand defineBrakeFailureRule = new DefineEvaluationRuleCommand(
            new InspectionItemId(UUID.randomUUID()),
            new RuleName("Brake system failure blocks the vehicle"),
            ResultValue.FAIL,
            RuleImpact.BLOCKING,
            null);

    @Test
    void definesARuleThatDoesNotExistYet() {
        when(evaluationRuleRepository.existsByInspectionItemIdAndFoundResultAndActiveTrue(
                defineBrakeFailureRule.inspectionItemId(), ResultValue.FAIL)).thenReturn(false);
        when(evaluationRuleRepository.save(any(EvaluationRule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = evaluationRuleCommandService.handle(defineBrakeFailureRule);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.success()).get().extracting(EvaluationRule::getResultingImpact).isEqualTo(RuleImpact.BLOCKING);
    }

    @Test
    void rejectsASecondActiveRuleForTheSameItemAndResult() {
        when(evaluationRuleRepository.existsByInspectionItemIdAndFoundResultAndActiveTrue(
                defineBrakeFailureRule.inspectionItemId(), ResultValue.FAIL)).thenReturn(true);

        var result = evaluationRuleCommandService.handle(defineBrakeFailureRule);

        assertThat(result.failure()).get().isInstanceOf(EvaluationRuleCommandFailure.Duplicate.class);
        verify(evaluationRuleRepository, never()).save(any());
    }

    @Test
    void deactivatesAnActiveRule() {
        var rule = new EvaluationRule(defineBrakeFailureRule);
        var id = UUID.randomUUID();
        when(evaluationRuleRepository.findById(id)).thenReturn(Optional.of(rule));
        when(evaluationRuleRepository.save(rule)).thenReturn(rule);

        var result = evaluationRuleCommandService.handle(new DeactivateEvaluationRuleCommand(id));

        assertThat(result.isSuccess()).isTrue();
        assertThat(rule.isActive()).isFalse();
    }

    @Test
    void reportsARuleThatDoesNotExist() {
        var id = UUID.randomUUID();
        when(evaluationRuleRepository.findById(id)).thenReturn(Optional.empty());

        var result = evaluationRuleCommandService.handle(new DeactivateEvaluationRuleCommand(id));

        assertThat(result.failure()).get().isInstanceOf(EvaluationRuleCommandFailure.NotFound.class);
    }

    @Test
    void rejectsDeactivatingARuleTwice() {
        var rule = new EvaluationRule(defineBrakeFailureRule);
        rule.deactivate();
        var id = UUID.randomUUID();
        when(evaluationRuleRepository.findById(id)).thenReturn(Optional.of(rule));

        var result = evaluationRuleCommandService.handle(new DeactivateEvaluationRuleCommand(id));

        assertThat(result.failure()).get().isInstanceOf(EvaluationRuleCommandFailure.AlreadyInactive.class);
        verify(evaluationRuleRepository, never()).save(any());
    }
}
