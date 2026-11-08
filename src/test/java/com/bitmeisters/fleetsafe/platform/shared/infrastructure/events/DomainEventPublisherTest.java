package com.bitmeisters.fleetsafe.platform.shared.infrastructure.events;

import com.bitmeisters.fleetsafe.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DomainEventPublisherTest {

    static class SampleAggregate extends AbstractDomainAggregateRoot<SampleAggregate> {
        void record(Object event) {
            registerDomainEvent(event);
        }
    }

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private DomainEventPublisher domainEventPublisher;

    @Test
    void publishesThePendingEventsInOrderAndClearsThem() {
        var aggregate = new SampleAggregate();
        aggregate.record("first");
        aggregate.record("second");

        domainEventPublisher.publishAndClearEvents(aggregate);

        InOrder order = inOrder(applicationEventPublisher);
        order.verify(applicationEventPublisher).publishEvent("first");
        order.verify(applicationEventPublisher).publishEvent("second");
        assertThat(aggregate.domainEvents()).isEmpty();
    }

    @Test
    void doesNotPublishTheSameEventsTwice() {
        var aggregate = new SampleAggregate();
        aggregate.record("only");

        domainEventPublisher.publishAndClearEvents(aggregate);
        domainEventPublisher.publishAndClearEvents(aggregate);

        verify(applicationEventPublisher).publishEvent("only");
    }

    @Test
    void ignoresAMissingAggregate() {
        domainEventPublisher.publishAndClearEvents(null);

        verify(applicationEventPublisher, never()).publishEvent(any(Object.class));
    }
}
