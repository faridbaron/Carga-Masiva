package co.com.bancolombia.api;

import co.com.bancolombia.usecase.audit.AuditUseCase;
import co.com.bancolombia.usecase.uploadmovements.UploadMovementsUseCase;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class MovementHandler {

    private final UploadMovementsUseCase movementsUseCase;
    private final AuditUseCase auditUseCase;

    public MovementHandler(UploadMovementsUseCase movementsUseCase, AuditUseCase auditUseCase) {
        this.movementsUseCase = movementsUseCase;
        this.auditUseCase = auditUseCase;
    }

    public Mono<ServerResponse> uploadMovements(ServerRequest request) {
        String boxId = request.pathVariable("boxId");
        AtomicReference<String> filename = new AtomicReference<>(null);
        AtomicReference<Integer> totalSize = new AtomicReference<>(0);
        String user = Optional.ofNullable(request.headers().firstHeader("X-User"))
                .filter(h -> !h.isEmpty())
                .orElse("anonymous");
        return request.multipartData()
                .flatMap(parts -> {
                    Part filePart = parts.toSingleValueMap().get("file");
                    if (filePart == null) {
                        return Mono.error(new RuntimeException("File part is missing in the request"));
                    }
                    filename.set(filePart.headers().getContentDisposition().getFilename());
                    if (filename.get() == null || (!filename.get().endsWith(".txt") && !filename.get().endsWith(".csv"))) {
                        return Mono.error(new RuntimeException("Invalid file type: Only txt/csv files are allowed"));
                    }
                    return filePart.content()
                            .map(dataBuffer -> {
                                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(bytes);
                                DataBufferUtils.release(dataBuffer);
                                return ByteBuffer.wrap(bytes);
                            })
                            .collectList()
                            .flatMap(byteBuffers -> {
                                totalSize.set(byteBuffers.stream()
                                        .mapToInt(ByteBuffer::remaining)
                                        .sum());
                                if (totalSize.get() > 5 * 1024 * 1024) {
                                    return Mono.error(new RuntimeException("File size exceeds 5MB limit"));
                                }
                                return movementsUseCase.uploadCSV(boxId, Flux.fromIterable(byteBuffers), user)
                                        .flatMap(report -> auditUseCase.logUpload(boxId, user, report, null,
                                                        filename.get(), totalSize.get())
                                                .thenReturn(report))
                                        .flatMap(report -> ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(report));
                            });
                })
                .onErrorResume(e ->
                        auditUseCase.logUpload(boxId, user, null, e, filename.get(), totalSize.get())
                                .then(ServerResponse.status(500)
                                        .bodyValue("Error processing file upload: " + e.getMessage()))
                );
    }
}
