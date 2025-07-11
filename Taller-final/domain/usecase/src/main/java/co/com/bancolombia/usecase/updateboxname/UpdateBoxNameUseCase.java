package co.com.bancolombia.usecase.updateboxname;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.events.model.BoxNameUpdatedEvent;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;

public class UpdateBoxNameUseCase {

    private final BoxRepository boxRepository;
    private final EventsGateway eventsGateway;

    public UpdateBoxNameUseCase(BoxRepository boxRepository, EventsGateway eventsGateway) {
        this.boxRepository = boxRepository;
        this.eventsGateway = eventsGateway;
    }

    public Mono<Box> updateBoxName(String boxId, String newName, String responsibleUser) {
        return boxRepository.findById(boxId)
                .flatMap(box -> {
                    String oldName = box.getName();
                    box.updateName(newName);
                    return boxRepository.save(box)
                            .flatMap(savedBox -> {
                                BoxNameUpdatedEvent event = new BoxNameUpdatedEvent(
                                        boxId,
                                        oldName,
                                        newName,
                                        LocalDateTime.now(),
                                        responsibleUser);
                                return eventsGateway.emitBoxNameUpdateEvent(event)
                                        .thenReturn(savedBox)
                                        .onErrorResume(e -> Mono.just(savedBox));
                            });
                });
    }
}