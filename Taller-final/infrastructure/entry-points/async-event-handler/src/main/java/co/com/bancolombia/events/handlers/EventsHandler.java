package co.com.bancolombia.events.handlers;

import org.reactivecommons.api.domain.DomainEvent;
import org.reactivecommons.async.impl.config.annotations.EnableEventListeners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

@EnableEventListeners
public class EventsHandler {
    private static final Logger log = LoggerFactory.getLogger(EventsHandler.class);

    public EventsHandler(/*SampleUseCase sampleUseCase*/) {
        //this.sampleUseCase = sampleUseCase;
    }

    public Mono<Void> handleEventCreated(DomainEvent<Object> event) {
        log.info("Event received: {} -> {}", event.getName(), event.getData());
        return Mono.empty();
    }

    public Mono<Void> handleEventUpdate(DomainEvent<Object> event) {
        log.info("Event received: {} -> {}", event.getName(), event.getData());
        return Mono.empty();
    }

    public Mono<Void> handleEventDeleted(DomainEvent<Object> event) {
        log.info("Event received: {} -> {}", event.getName(), event.getData());
        return Mono.empty();
    }
    public Mono<Void> handleEventReopened(DomainEvent<Object> event) {
        log.info("Event received: {} -> {}", event.getName(), event.getData());
        return Mono.empty();
    }
    public Mono<Void> handleEventUploaded(DomainEvent<Object> event) {
        log.info("Event received: {} -> {}", event.getName(), event.getData());
        return Mono.empty();
    }

}
