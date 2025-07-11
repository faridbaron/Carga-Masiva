package co.com.bancolombia.usecase.reopenbox;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.BoxStatus;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.events.model.BoxReopenedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReopenBoxUseCaseTest {

    @Mock
    private BoxRepository boxRepository;

    @Mock
    private EventsGateway eventsGateway;

    @InjectMocks
    private ReopenBoxUseCase reopenBoxUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void reopenBox_success() {
        String boxId = "box123";
        String responsibleUser = "user123";
        Box box = new Box();
        box.setId(boxId);
        box.setStatus(BoxStatus.CLOSED);

        when(boxRepository.findById(boxId)).thenReturn(Mono.just(box));
        when(boxRepository.save(any(Box.class))).thenReturn(Mono.just(box));
        when(eventsGateway.emitBoxReopenedEvent(any(BoxReopenedEvent.class))).thenReturn(Mono.empty());

        StepVerifier.create(reopenBoxUseCase.reopenBox(boxId, responsibleUser))
                .expectNextMatches(reopenedBox -> reopenedBox.getId().equals(boxId))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verify(boxRepository).save(any(Box.class));
        verify(eventsGateway).emitBoxReopenedEvent(any(BoxReopenedEvent.class));
    }

    @Test
    void reopenBox_boxNotFound() {
        String boxId = "box123";
        String responsibleUser = "user123";

        when(boxRepository.findById(boxId)).thenReturn(Mono.empty());

        StepVerifier.create(reopenBoxUseCase.reopenBox(boxId, responsibleUser))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verifyNoInteractions(eventsGateway);
    }

    @Test
    void reopenBox_eventEmitError() {
        String boxId = "box123";
        String responsibleUser = "user123";
        Box box = new Box();
        box.setId(boxId);
        box.setStatus(BoxStatus.CLOSED);


        when(boxRepository.findById(boxId)).thenReturn(Mono.just(box));
        when(boxRepository.save(any(Box.class))).thenReturn(Mono.just(box));
        when(eventsGateway.emitBoxReopenedEvent(any(BoxReopenedEvent.class))).thenReturn(Mono.error(new RuntimeException("Event error")));

        StepVerifier.create(reopenBoxUseCase.reopenBox(boxId, responsibleUser))
                .expectNextMatches(reopenedBox -> reopenedBox.getId().equals(boxId))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verify(boxRepository).save(any(Box.class));
        verify(eventsGateway).emitBoxReopenedEvent(any(BoxReopenedEvent.class));
    }
}