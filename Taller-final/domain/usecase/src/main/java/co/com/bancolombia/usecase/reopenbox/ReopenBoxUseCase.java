package co.com.bancolombia.usecase.reopenbox;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.events.model.BoxReopenedEvent;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class ReopenBoxUseCase {

    private final BoxRepository boxRepository;
    private final EventsGateway eventsGateway;

    public ReopenBoxUseCase(BoxRepository boxRepository, EventsGateway eventsGateway) {
        this.boxRepository = boxRepository;
        this.eventsGateway = eventsGateway;
    }

    public Mono<Box> reopenBox(String boxId, String responsibleUser) {
        return boxRepository.findById(boxId)
                .flatMap(box -> {
                    box.reopen();
                    return boxRepository.save(box)
                            .flatMap(savedBox -> {
                                BoxReopenedEvent event = new BoxReopenedEvent(
                                        savedBox,
                                        responsibleUser,
                                        LocalDateTime.now()
                                );
                                return eventsGateway.emitBoxReopenedEvent(event)
                                        .thenReturn(savedBox)
                                        .onErrorResume(e -> Mono.just(savedBox));
                            });
                });
    }
}
