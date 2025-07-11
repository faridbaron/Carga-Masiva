package co.com.bancolombia.usecase.uploadmovements;
import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.movement.Movement;
import co.com.bancolombia.model.movement.UploadReport;
import co.com.bancolombia.model.movement.gateways.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UploadMovementsUseCaseTest {

    @Mock
    private MovementRepository movementRepository;
    @Mock
    private RenderFileGateway renderFileGateway;
    @Mock
    private BoxRepository boxRepository;
    @Mock
    private EventsGateway eventsGateway;

    @InjectMocks
    private UploadMovementsUseCase uploadMovementsUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadCSV_success() {
        // Mock data
        String boxId = "box123";
        String responsibleUser = "user123";
        ByteBuffer byteBuffer = ByteBuffer.wrap("test-data".getBytes());
        Map<String, String> record = Map.of(
                "movementId", "mov1",
                "type", "INCOME",
                "amount", "100.00",
                "currency", "COP",
                "description", "Test movement",
                "date", LocalDateTime.now().toString(),
                "boxId", boxId
        );
        Movement movement = new Movement();
        movement.setMovementId("mov1");
        movement.setBoxId(boxId);
        movement.setAmount(new BigDecimal("100.00"));

        Box box = new Box();

        // Mock behaviors
        when(boxRepository.findById(boxId)).thenReturn(Mono.just(box));
        when(renderFileGateway.render(any())).thenReturn(Flux.just(record));
        when(movementRepository.save(any(Movement.class))).thenReturn(Mono.just(movement));
        when(eventsGateway.emitBoxUploadedEvent(any(UploadReport.class))).thenReturn(Mono.empty());

        // Execute use case
        StepVerifier.create(uploadMovementsUseCase.uploadCSV(boxId, Flux.just(byteBuffer), responsibleUser))
                .expectNextMatches(uploadReport -> uploadReport.getSuccess() == 1)
                .verifyComplete();

        // Verify interactions
        verify(boxRepository).findById(boxId);
        verify(renderFileGateway).render(any());
        verify(movementRepository).save(any(Movement.class));
        verify(eventsGateway).emitBoxUploadedEvent(any(UploadReport.class));
    }

    @Test
    void uploadCSV_boxNotFound() {
        String boxId = "box123";
        String responsibleUser = "user123";
        ByteBuffer byteBuffer = ByteBuffer.wrap("test-data".getBytes());

        when(boxRepository.findById(boxId)).thenReturn(Mono.empty());

        StepVerifier.create(uploadMovementsUseCase.uploadCSV(boxId, Flux.just(byteBuffer), responsibleUser))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("BoxId not found"))
                .verify();

        verify(boxRepository).findById(boxId);
        verifyNoInteractions(renderFileGateway, movementRepository, eventsGateway);
    }

    @Test
    void uploadCSV_invalidRecord() {
        String boxId = "box123";
        String responsibleUser = "user123";
        ByteBuffer byteBuffer = ByteBuffer.wrap("test-data".getBytes());
        Map<String, String> invalidRecord = Map.of(
                "movementId", "mov1",
                "type", "INVALID_TYPE",
                "amount", "100.00",
                "currency", "COP",
                "description", "Test movement",
                "date", LocalDateTime.now().toString(),
                "boxId", boxId
        );

        Box box = new Box();

        when(boxRepository.findById(boxId)).thenReturn(Mono.just(box));
        when(renderFileGateway.render(any())).thenReturn(Flux.just(invalidRecord));
        when(eventsGateway.emitBoxUploadedEvent(any(UploadReport.class))).thenReturn(Mono.empty());
        StepVerifier.create(uploadMovementsUseCase.uploadCSV(boxId, Flux.just(byteBuffer), responsibleUser))
                .expectNextMatches(uploadReport -> uploadReport.getFailed() == 1)
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verify(renderFileGateway).render(any());
        verifyNoInteractions(movementRepository);
    }

    @Test
    void uploadCSV_eventEmitError() {
        String boxId = "box123";
        String responsibleUser = "user123";
        ByteBuffer byteBuffer = ByteBuffer.wrap("test-data".getBytes());
        Map<String, String> record = Map.of(
                "movementId", "mov1",
                "type", "INCOME",
                "amount", "100.00",
                "currency", "COP",
                "description", "Test movement",
                "date", LocalDateTime.now().toString(),
                "boxId", boxId
        );
        Movement movement = new Movement();
        movement.setMovementId("mov1");
        movement.setBoxId(boxId);
        movement.setAmount(new BigDecimal("100.00"));

        Box box = new Box();

        when(boxRepository.findById(boxId)).thenReturn(Mono.just(box));
        when(renderFileGateway.render(any())).thenReturn(Flux.just(record));
        when(movementRepository.save(any(Movement.class))).thenReturn(Mono.just(movement));
        when(eventsGateway.emitBoxUploadedEvent(any(UploadReport.class))).thenReturn(Mono.error(new RuntimeException("Event error")));

        StepVerifier.create(uploadMovementsUseCase.uploadCSV(boxId, Flux.just(byteBuffer), responsibleUser))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Event error"))
                .verify();

        verify(boxRepository).findById(boxId);
        verify(renderFileGateway).render(any());
        verify(movementRepository).save(any(Movement.class));
        verify(eventsGateway).emitBoxUploadedEvent(any(UploadReport.class));
    }
}