package co.com.bancolombia.usecase.createbox;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.BoxStatus;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateBoxUseCaseTest {

    @Mock
    private BoxRepository boxRepository;

    @Mock
    private EventsGateway eventsGateway;

    @InjectMocks
    private CreateBoxUseCase createBoxUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createBox_success() {
        String boxId = "box123";
        String boxName = "Test Box";
        Box box = new Box.Builder()
                .id(boxId)
                .name(boxName)
                .status(BoxStatus.CLOSED)
                .currentBalance(BigDecimal.ZERO)
                .build();

        when(boxRepository.findById(boxId)).thenReturn(Mono.empty());
        when(boxRepository.save(any(Box.class))).thenReturn(Mono.just(box));
        when(eventsGateway.emitBoxCreatedEvent(any(Box.class))).thenReturn(Mono.empty());

        StepVerifier.create(createBoxUseCase.createBox(boxId, boxName))
                .expectNextMatches(createdBox -> createdBox.getId().equals(boxId) && createdBox.getName().equals(boxName))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verify(boxRepository).save(any(Box.class));
        verify(eventsGateway).emitBoxCreatedEvent(any(Box.class));
    }

    @Test
    void createBox_alreadyExists() {
        String boxId = "box123";
        String boxName = "Test Box";
        Box existingBox = new Box.Builder()
                .id(boxId)
                .name(boxName)
                .status(BoxStatus.CLOSED)
                .currentBalance(BigDecimal.ZERO)
                .build();

        when(boxRepository.findById(boxId)).thenReturn(Mono.just(existingBox));

        StepVerifier.create(createBoxUseCase.createBox(boxId, boxName))
                .expectErrorMatches(throwable -> throwable instanceof IllegalStateException &&
                        throwable.getMessage().equals("La caja ya existe"))
                .verify();

        verify(boxRepository).findById(boxId);
        verifyNoMoreInteractions(boxRepository, eventsGateway);
    }

    @Test
    void createBox_eventEmitError() {
        String boxId = "box123";
        String boxName = "Test Box";
        Box box = new Box.Builder()
                .id(boxId)
                .name(boxName)
                .status(BoxStatus.CLOSED)
                .currentBalance(BigDecimal.ZERO)
                .build();

        when(boxRepository.findById(boxId)).thenReturn(Mono.empty());
        when(boxRepository.save(any(Box.class))).thenReturn(Mono.just(box));
        when(eventsGateway.emitBoxCreatedEvent(any(Box.class))).thenReturn(Mono.error(new RuntimeException("Event error")));

        StepVerifier.create(createBoxUseCase.createBox(boxId, boxName))
                .expectNextMatches(createdBox -> createdBox.getId().equals(boxId) && createdBox.getName().equals(boxName))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verify(boxRepository).save(any(Box.class));
        verify(eventsGateway).emitBoxCreatedEvent(any(Box.class));
    }
}