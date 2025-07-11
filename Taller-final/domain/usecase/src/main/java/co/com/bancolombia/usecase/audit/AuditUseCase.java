package co.com.bancolombia.usecase.audit;

import co.com.bancolombia.model.audit.Audit;
import co.com.bancolombia.model.audit.gateways.AuditRepository;
import co.com.bancolombia.model.movement.UploadReport;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public class AuditUseCase {

    private final AuditRepository auditRepository;

    public AuditUseCase(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    public Mono<Void> logUpload(String boxId, String user, UploadReport report, Throwable error,
                                String filename, int fileSizeBytes) {

        String status = (error == null) ? "SUCCESS" : "FAILURE";
        String details;
        if (report != null) {
            details = String.format("Processed: %d, Success: %d, Errors: %d",
                    report.getTotal(), report.getSuccess(), report.getFailed());
        } else if (error != null) {
            details = "Error: " + error.getMessage();
        } else {
            details = "Unknown result";
        }
        Audit audit = new Audit(
                boxId,
                user,
                "UPLOAD",
                status,
                filename,
                fileSizeBytes / (1024 * 1024) ,
                details,
                LocalDateTime.now());
        return auditRepository.save(audit)
                .then();
    }
}
