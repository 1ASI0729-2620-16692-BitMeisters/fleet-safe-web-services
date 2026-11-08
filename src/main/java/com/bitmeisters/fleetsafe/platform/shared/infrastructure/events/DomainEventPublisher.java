package com.bitmeisters.fleetsafe.platform.shared.infrastructure.events;

import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public DomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public <T extends AbstractDomainAggregateRoot<T>> void publishAndClearEvents(T aggregate) {
        if (aggregate == null) return;
        var events = List.copyOf(aggregate.domainEvents());
        events.forEach(applicationEventPublisher::publishEvent);
        aggregate.clearDomainEvents();
    }
}
