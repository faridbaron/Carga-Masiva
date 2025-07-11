package co.com.bancolombia.events;

import co.com.bancolombia.model.events.gateways.EventsGateway;
import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.api.domain.DomainEventBus;
import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static reactor.core.publisher.Mono.from;

@EnableDomainEventBus
public class ReactiveEventsGateway implements EventsGateway {
    public static final String BOX_CREATED = "box.event.created";
    public static final String BOX_UPDATED = "box.event.name.updated";
    public static final String BOX_DELETED = "box.event.deleted";
    public static final String BOX_REOPENED = "box.event.reopened";
    public static final String MOVEMENT_UPLOADED = "movement.event.uploaded";
    private final DomainEventBus domainEventBus;

    public ReactiveEventsGateway(DomainEventBus domainEventBus) {
        this.domainEventBus = domainEventBus;
    }

    @Override
    public Mono<Void> emitBoxCreatedEvent(Object event) {
         return from(domainEventBus.emit(new DomainEvent<>(BOX_CREATED, UUID.randomUUID().toString(), event)));
    }

    @Override
    public Mono<Void> emitBoxNameUpdateEvent(Object event) {
        return from(domainEventBus.emit(new DomainEvent<>(BOX_UPDATED, UUID.randomUUID().toString(), event)));
    }

    @Override
    public Mono<Void> emitBoxDeletedEvent(Object event) {
        return from(domainEventBus.emit(new DomainEvent<>(BOX_DELETED, UUID.randomUUID().toString(), event)));
    }

    @Override
    public Mono<Void> emitBoxReopenedEvent(Object event) {
        return from(domainEventBus.emit(new DomainEvent<>(BOX_REOPENED, UUID.randomUUID().toString(), event)));
    }
    @Override
    public Mono<Void> emitBoxUploadedEvent(Object event) {
        return from(domainEventBus.emit(new DomainEvent<>(MOVEMENT_UPLOADED, UUID.randomUUID().toString(), event)));
    }

}
