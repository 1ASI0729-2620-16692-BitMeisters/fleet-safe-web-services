package com.bitmeisters.fleetsafe.platform.evaluation.application.internal.commandservices;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.OperationalAuthorizationCommandFailure;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.Evaluation;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.aggregates.OperationalAuthorization;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.EvaluateInspectionCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.commands.LiftBlockCommand;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionItemId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.InspectionResultSnapshot;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.ResultValue;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleImpact;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.RuleName;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.model.valueobjects.VehicleId;
import com.bitmeisters.fleetsafe.platform.evaluation.domain.repositories.OperationalAuthorizationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperationalAuthorizationCommandServiceImplTest {

    @Mock
    private OperationalAuthorizationRepository operationalAuthorizationRepository;

    @InjectMocks
    private OperationalAuthorizationCommandServiceImpl operationalAuthorizationCommandService;

    private final VehicleId truck = new VehicleId(UUID.randomUUID());
    private final LiftBlockCommand liftBlock = new LiftBlockCommand(truck, "Urgent delivery", UUID.randomUUID());

    private OperationalAuthorization enabledAuthorization() {
        var evaluation = new Evaluation(new EvaluateInspectionCommand(new InspectionId(UUID.randomUUID()), truck,
                List.of(new InspectionResultSnapshot(new InspectionResultId(UUID.randomUUID()),
                        new InspectionItemId(UUID.randomUUID()), ResultValue.OK)), null), List.of());
        return new OperationalAuthorization(evaluation, truck);
    }

    @Test
    void reportsAVehicleWithoutAuthorization() {
        when(operationalAuthorizationRepository.findFirstByVehicleIdOrderByAuthorizedAtDesc(truck)).thenReturn(Optional.empty());

        var result = operationalAuthorizationCommandService.handle(liftBlock);

        assertThat(result.failure()).get().isInstanceOf(OperationalAuthorizationCommandFailure.NotFound.class);
    }

    @Test
    void rejectsLiftingTheBlockOfAVehicleThatIsNotBlocked() {
        when(operationalAuthorizationRepository.findFirstByVehicleIdOrderByAuthorizedAtDesc(truck)).thenReturn(Optional.of(enabledAuthorization()));

        var result = operationalAuthorizationCommandService.handle(liftBlock);

        assertThat(result.failure()).get().isInstanceOf(OperationalAuthorizationCommandFailure.NotBlocked.class);
        verify(operationalAuthorizationRepository, never()).save(any());
    }
}
