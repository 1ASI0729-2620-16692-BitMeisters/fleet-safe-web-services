package com.bitmeisters.fleetsafe.platform.preoperationalinspection.application.internal.eventhandlers;

import com.bitmeisters.fleetsafe.platform.preoperationalinspection.domain.model.events.InspectionCompletedEvent;
import com.bitmeisters.fleetsafe.platform.preoperationalinspection.interfaces.events.InspectionCompletedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class InspectionCompletedEventHandler {

    private final ApplicationEventPublisher eventPublisher;

    public InspectionCompletedEventHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void on(InspectionCompletedEvent event) {
        eventPublisher.publishEvent(new InspectionCompletedIntegrationEvent(
                event.inspectionId().value(), event.vehicleId().value(), event.driverId().value()));
    }
}
