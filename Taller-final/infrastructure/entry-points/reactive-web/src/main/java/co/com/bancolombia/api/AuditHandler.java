package co.com.bancolombia.api;

import co.com.bancolombia.model.audit.Audit;
import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.usecase.gethistoryaudit.GetHistoryAuditUseCase;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class AuditHandler {

    private final GetHistoryAuditUseCase getHistoryAuditUseCase;

    public AuditHandler(GetHistoryAuditUseCase getHistoryAuditUseCase) {
        this.getHistoryAuditUseCase = getHistoryAuditUseCase;
    }

    public Mono<ServerResponse> getAllHistoryAudit(ServerRequest request) {
        return ServerResponse.ok()
                .body(BodyInserters.fromPublisher(getHistoryAuditUseCase.getHistory(), Audit.class));
    }

}
