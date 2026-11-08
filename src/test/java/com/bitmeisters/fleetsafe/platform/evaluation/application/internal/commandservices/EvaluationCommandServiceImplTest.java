package com.bitmeisters.fleetsafe.platform.evaluation.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationCommandFailure;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.Evaluation;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.EvaluationRule;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
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
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.EvaluationRepository;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.EvaluationRuleRepository;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.OperationalAuthorizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationCommandServiceImplTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private EvaluationRuleRepository evaluationRuleRepository;

    @Mock
    private OperationalAuthorizationRepository operationalAuthorizationRepository;

    @InjectMocks
    private EvaluationCommandServiceImpl evaluationCommandService;

    private final InspectionItemId brakes = new InspectionItemId(UUID.randomUUID());

    private EvaluateInspectionCommand inspectionWith(ResultValue brakesResult) {
        return new EvaluateInspectionCommand(
                new InspectionId(UUID.randomUUID()),
                new VehicleId(UUID.randomUUID()),
                List.of(new InspectionResultSnapshot(new InspectionResultId(UUID.randomUUID()), brakes, brakesResult)),
                null);
    }

    @Test
    void blocksTheVehicleWhenABlockingRuleApplies() {
        var command = inspectionWith(ResultValue.FAIL);
        when(evaluationRepository.existsByInspectionId(command.inspectionId())).thenReturn(false);
        when(evaluationRuleRepository.findAllByActiveTrue()).thenReturn(List.of(new EvaluationRule(
                new DefineEvaluationRuleCommand(brakes, new RuleName("Brakes"), ResultValue.FAIL, RuleImpact.BLOCKING, null))));
        when(evaluationRepository.save(any(Evaluation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(operationalAuthorizationRepository.save(any(OperationalAuthorization.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = evaluationCommandService.handle(command);

        assertThat(result.success()).get().extracting(OperationalAuthorization::getStatus).isEqualTo(AuthorizationStatus.NOT_ENABLED);
    }

    @Test
    void rejectsAnInspectionThatWasAlreadyEvaluated() {
        var command = inspectionWith(ResultValue.OK);
        when(evaluationRepository.existsByInspectionId(command.inspectionId())).thenReturn(true);

        var result = evaluationCommandService.handle(command);

        assertThat(result.failure()).get().isInstanceOf(EvaluationCommandFailure.AlreadyEvaluated.class);
        verify(evaluationRepository, never()).save(any());
        verify(operationalAuthorizationRepository, never()).save(any());
    }

    @Test
    void rejectsAnInspectionWithoutResults() {
        var command = new EvaluateInspectionCommand(new InspectionId(UUID.randomUUID()), new VehicleId(UUID.randomUUID()), List.of(), null);
        when(evaluationRepository.existsByInspectionId(command.inspectionId())).thenReturn(false);

        var result = evaluationCommandService.handle(command);

        assertThat(result.failure()).get().isInstanceOf(EvaluationCommandFailure.NothingToEvaluate.class);
        verify(evaluationRepository, never()).save(any());
    }
}
