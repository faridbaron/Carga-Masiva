package co.com.bancolombia.model.events.gateways;

import reactor.core.publisher.Mono;

public interface EventsGateway {
    Mono<Void> emitBoxCreatedEvent(Object event);
    Mono<Void> emitBoxNameUpdateEvent(Object event);
    Mono<Void> emitBoxDeletedEvent(Object event);
    Mono<Void> emitBoxReopenedEvent(Object event);
    Mono<Void> emitBoxUploadedEvent(Object event);
}
