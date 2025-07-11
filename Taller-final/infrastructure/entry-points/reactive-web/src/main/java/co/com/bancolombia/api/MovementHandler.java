package co.com.bancolombia.api;

import co.com.bancolombia.model.movement.UploadReport;
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
        String user = Optional.ofNullable(request.headers().firstHeader("X-User"))
                .filter(h -> !h.isEmpty())
                .orElse("anonymous");

        return request.multipartData()
                .flatMap(parts -> {
                    Part filePart = parts.toSingleValueMap().get("file");
                    if (filePart == null) {
                        return logAndRespond(boxId, user, null,
                                "File part is missing in the request", null, 0, 400);
                    }

                    String filename = filePart.headers().getContentDisposition().getFilename();
                    if (!isSupportedFileType(filename)) {
                        return logAndRespond(boxId, user, null,
                                "Invalid file type. Only .txt and .csv files are allowed.", filename, 0, 415);
                    }

                    return filePart.content()
                            .map(dataBuffer -> {
                                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(bytes);
                                DataBufferUtils.release(dataBuffer);
                                return ByteBuffer.wrap(bytes);
                            })
                            .collectList()
                            .flatMap(buffers -> {
                                int totalSize = buffers.stream().mapToInt(ByteBuffer::remaining).sum();
                                if (totalSize > 5 * 1024 * 1024) {
                                    return logAndRespond(boxId, user, null,
                                            "File size exceeds the limit of 5MB", filename, totalSize, 400);
                                }

                                return movementsUseCase.uploadCSV(boxId, Flux.fromIterable(buffers), user)
                                        .flatMap(report ->
                                                auditUseCase.logUpload(boxId, user, report, null, filename, totalSize)
                                                        .then(ServerResponse.ok()
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .bodyValue(report))
                                        );
                            });
                })
                .onErrorResume(RuntimeException.class, e -> {
                    int status = "BoxId not found".equals(e.getMessage()) ? 404 : 500;
                    return logAndRespond(boxId, user, null, e.getMessage(), null, 0, status);
                });
    }

    private boolean isSupportedFileType(String filename) {
        if (filename == null) return false;
        return filename.endsWith(".csv") || filename.endsWith(".txt");
    }

    private Mono<ServerResponse> logAndRespond(String boxId, String user, UploadReport report,
                                               String errorMsg, String filename, int size, int status) {
        return auditUseCase.logUpload(boxId, user, report, errorMsg, filename, size)
                .then(ServerResponse.status(status).bodyValue(errorMsg));
    }

}
