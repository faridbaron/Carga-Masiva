package co.com.bancolombia.usecase.deletebox;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.events.model.BoxDeletedEvent;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class DeleteBoxUseCase {

    private final BoxRepository boxRepository;
    private final EventsGateway eventsGateway;

    public DeleteBoxUseCase(BoxRepository boxRepository, EventsGateway eventsGateway) {
        this.boxRepository = boxRepository;
        this.eventsGateway = eventsGateway;
    }

    public Mono<Box> deleteBox(String boxId, String responsibleUser) {
        return boxRepository.findById(boxId)
                .flatMap(box -> {
                    box.delete();
                    return boxRepository.save(box)
                            .flatMap(savedBox -> {
                                BoxDeletedEvent event = new BoxDeletedEvent(
                                        savedBox.getId(),
                                        savedBox.getName(),
                                        responsibleUser,
                                        LocalDateTime.now()
                                );
                                return eventsGateway.emitBoxDeletedEvent(event)
                                        .thenReturn(savedBox)
                                        .onErrorResume(e -> Mono.just(savedBox));
                            });
                });
    }
}
