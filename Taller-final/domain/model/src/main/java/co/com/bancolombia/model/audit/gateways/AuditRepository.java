package co.com.bancolombia.model.audit.gateways;

import co.com.bancolombia.model.audit.Audit;
import co.com.bancolombia.model.movement.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuditRepository {
    Mono<Audit> save(Audit audit);
    Flux<Audit> findAll();
}
