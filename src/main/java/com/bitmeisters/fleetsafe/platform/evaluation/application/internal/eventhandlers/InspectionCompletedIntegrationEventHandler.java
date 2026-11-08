package com.bitmeisters.fleetsafe.platform.evaluation.application.internal.eventhandlers;

import com.bitmeisters.fleetsafe.platform.evaluation.application.commandservices.EvaluationCommandService;
import com.bitmeisters.fleetsafe.platform.evaluation.application.internal.outboundservices.acl.ExternalInspectionService;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.events.InspectionCompletedIntegrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InspectionCompletedIntegrationEventHandler {

    private final ExternalInspectionService externalInspectionService;
    private final EvaluationCommandService evaluationCommandService;

    public InspectionCompletedIntegrationEventHandler(ExternalInspectionService externalInspectionService,
                                                      EvaluationCommandService evaluationCommandService) {
        this.externalInspectionService = externalInspectionService;
        this.evaluationCommandService = evaluationCommandService;
    }

    @EventListener
    public void on(InspectionCompletedIntegrationEvent event) {
        externalInspectionService.evaluationOf(event.inspectionId(), null)
                .map(evaluationCommandService::handle)
                .ifPresentOrElse(
                        result -> result.failure().ifPresent(failure ->
                                log.warn("Completed inspection not evaluated: inspectionId={}, reason={}", event.inspectionId(), failure.messageKey())),
                        () -> log.warn("Completed inspection not found for evaluation: inspectionId={}", event.inspectionId()));
    }
}
