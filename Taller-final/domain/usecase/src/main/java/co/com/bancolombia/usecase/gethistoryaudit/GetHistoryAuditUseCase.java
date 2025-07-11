package co.com.bancolombia.usecase.gethistoryaudit;

import co.com.bancolombia.model.audit.Audit;
import co.com.bancolombia.model.audit.gateways.AuditRepository;
import reactor.core.publisher.Flux;

public class GetHistoryAuditUseCase {
    private final AuditRepository auditRepository;

    public GetHistoryAuditUseCase(AuditRepository auditRepository){
        this.auditRepository = auditRepository;
    }

    public Flux<Audit> getHistory() {
        return auditRepository.findAll();
    }
}
