package co.com.bancolombia.usecase.updateboxname;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.events.model.BoxNameUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateBoxNameUseCaseTest {

    @Mock
    private BoxRepository boxRepository;

    @Mock
    private EventsGateway eventsGateway;

    @InjectMocks
    private UpdateBoxNameUseCase updateBoxNameUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateBoxName_success() {
        String boxId = "box123";
        String oldName = "Old Box Name";
        String newName = "New Box Name";
        String responsibleUser = "user123";
        Box box = new Box();
        box.setId(boxId);
        box.setName(oldName);

        when(boxRepository.findById(boxId)).thenReturn(Mono.just(box));
        when(boxRepository.save(any(Box.class))).thenReturn(Mono.just(box));
        when(eventsGateway.emitBoxNameUpdateEvent(any(BoxNameUpdatedEvent.class))).thenReturn(Mono.empty());

        StepVerifier.create(updateBoxNameUseCase.updateBoxName(boxId, newName, responsibleUser))
                .expectNextMatches(updatedBox -> updatedBox.getId().equals(boxId) && updatedBox.getName().equals(newName))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verify(boxRepository).save(any(Box.class));
        verify(eventsGateway).emitBoxNameUpdateEvent(any(BoxNameUpdatedEvent.class));
    }

    @Test
    void updateBoxName_boxNotFound() {
        String boxId = "box123";
        String newName = "New Box Name";
        String responsibleUser = "user123";

        when(boxRepository.findById(boxId)).thenReturn(Mono.empty());

        StepVerifier.create(updateBoxNameUseCase.updateBoxName(boxId, newName, responsibleUser))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verifyNoInteractions(eventsGateway);
    }

    @Test
    void updateBoxName_eventEmitError() {
        String boxId = "box123";
        String oldName = "Old Box Name";
        String newName = "New Box Name";
        String responsibleUser = "user123";
        Box box = new Box();
        box.setId(boxId);
        box.setName(oldName);

        when(boxRepository.findById(boxId)).thenReturn(Mono.just(box));
        when(boxRepository.save(any(Box.class))).thenReturn(Mono.just(box));
        when(eventsGateway.emitBoxNameUpdateEvent(any(BoxNameUpdatedEvent.class))).thenReturn(Mono.error(new RuntimeException("Event error")));

        StepVerifier.create(updateBoxNameUseCase.updateBoxName(boxId, newName, responsibleUser))
                .expectNextMatches(updatedBox -> updatedBox.getId().equals(boxId) && updatedBox.getName().equals(newName))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verify(boxRepository).save(any(Box.class));
        verify(eventsGateway).emitBoxNameUpdateEvent(any(BoxNameUpdatedEvent.class));
    }
}