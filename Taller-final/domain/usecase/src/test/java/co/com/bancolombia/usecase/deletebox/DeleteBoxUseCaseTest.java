package co.com.bancolombia.usecase.deletebox;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.events.model.BoxDeletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeleteBoxUseCaseTest {

    @Mock
    private BoxRepository boxRepository;

    @Mock
    private EventsGateway eventsGateway;

    @InjectMocks
    private DeleteBoxUseCase deleteBoxUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteBox_success() {
        String boxId = "box123";
        String responsibleUser = "user123";
        Box box = new Box();
        box.setId(boxId);
        box.setName("Test Box");

        when(boxRepository.findById(boxId)).thenReturn(Mono.just(box));
        when(boxRepository.save(any(Box.class))).thenReturn(Mono.just(box));
        when(eventsGateway.emitBoxDeletedEvent(any(BoxDeletedEvent.class))).thenReturn(Mono.empty());

        StepVerifier.create(deleteBoxUseCase.deleteBox(boxId, responsibleUser))
                .expectNextMatches(deletedBox -> deletedBox.getId().equals(boxId))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verify(boxRepository).save(any(Box.class));
        verify(eventsGateway).emitBoxDeletedEvent(any(BoxDeletedEvent.class));
    }

    @Test
    void deleteBox_boxNotFound() {
        String boxId = "box123";
        String responsibleUser = "user123";

        when(boxRepository.findById(boxId)).thenReturn(Mono.empty());

        StepVerifier.create(deleteBoxUseCase.deleteBox(boxId, responsibleUser))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verifyNoMoreInteractions(boxRepository, eventsGateway);
    }

    @Test
    void deleteBox_eventEmitError() {
        String boxId = "box123";
        String responsibleUser = "user123";
        Box box = new Box();
        box.setId(boxId);
        box.setName("Test Box");

        when(boxRepository.findById(boxId)).thenReturn(Mono.just(box));
        when(boxRepository.save(any(Box.class))).thenReturn(Mono.just(box));
        when(eventsGateway.emitBoxDeletedEvent(any(BoxDeletedEvent.class))).thenReturn(Mono.error(new RuntimeException("Event error")));

        StepVerifier.create(deleteBoxUseCase.deleteBox(boxId, responsibleUser))
                .expectNextMatches(deletedBox -> deletedBox.getId().equals(boxId))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verify(boxRepository).save(any(Box.class));
        verify(eventsGateway).emitBoxDeletedEvent(any(BoxDeletedEvent.class));
    }
}