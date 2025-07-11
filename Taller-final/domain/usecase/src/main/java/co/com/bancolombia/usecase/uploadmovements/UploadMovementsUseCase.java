package co.com.bancolombia.usecase.uploadmovements;

import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.events.gateways.EventsGateway;
import co.com.bancolombia.model.movement.Movement;
import co.com.bancolombia.model.movement.MovementType;
import co.com.bancolombia.model.movement.UploadReport;
import co.com.bancolombia.model.movement.gateways.MovementRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class UploadMovementsUseCase {

    private static final Set<String> ALLOWED_CURRENCIES = Set.of("COP", "USD");
    private final MovementRepository movementRepository;
    private final RenderFileGateway renderFileGateway;
    private final BoxRepository boxRepository;
    private final EventsGateway eventsGateway;

    public UploadMovementsUseCase(MovementRepository movementRepository, RenderFileGateway renderFileGateway,
                                  BoxRepository boxRepository, EventsGateway eventsGateway) {
        this.movementRepository = movementRepository;
        this.renderFileGateway = renderFileGateway;
        this.boxRepository = boxRepository;
        this.eventsGateway = eventsGateway;
    }

    public Mono<UploadReport> uploadCSV(String boxId, Flux<ByteBuffer> byteBuffer, String responsibleUser) {
        Set<String> processedMovementIds = new HashSet<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        return boxRepository.findById(boxId)
                .switchIfEmpty(Mono.error(new RuntimeException("Box not found with id: " + boxId)))
                .thenMany(byteBuffer.map(ByteBuffer::array)
                        .flatMap(renderFileGateway::render))
                .flatMap(recordMovement -> validateAndSave(boxId, recordMovement, processedMovementIds)
                        .doOnSuccess(movement -> successCount.incrementAndGet())
                        .onErrorResume(e -> {
                            System.out.println("Error processing record: " + recordMovement + ". Error: " + e.getMessage());
                            errorCount.incrementAndGet();
                            return Mono.empty();
                        })
                )
                .then(Mono.defer(() -> {
                    UploadReport uploadReport = new UploadReport(
                            boxId,
                            successCount.get() + errorCount.get(),
                            successCount.get(),
                            errorCount.get(),
                            LocalDateTime.now(),
                            responsibleUser);
                    return eventsGateway.emitBoxUploadedEvent(uploadReport)
                            .then(Mono.just(uploadReport));
                }));
    }

    private Mono<Movement> validateAndSave(String boxId, Map<String, String> recordMovements,
                                           Set<String> processedMovementIds) {
        try {
            Movement movement = Movement.toRecord(recordMovements, boxId, processedMovementIds);
            return movementRepository.save(movement);
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Error processing record: " + recordMovements + ". Error: " +
                    e.getMessage()));
        }
    }

    private boolean isValidISO8601(String date) {
        try {
            DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_DATE_TIME;
            LocalDateTime.parse(date, isoFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

}
