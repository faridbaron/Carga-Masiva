package co.com.bancolombia.mongo;

import co.com.bancolombia.model.audit.Audit;
import co.com.bancolombia.model.audit.gateways.AuditRepository;
import co.com.bancolombia.mongo.helper.AdapterOperations;
import co.com.bancolombia.mongo.model.AuditDocument;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MongoAuditRepositoryAdapter extends AdapterOperations<Audit, AuditDocument, String, MongoDBAuditRepository>
 implements AuditRepository
{

    public MongoAuditRepositoryAdapter(MongoDBAuditRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Audit.class));
    }


    public Mono<Audit> save(Audit audit){
        AuditDocument auditDocument = new AuditDocument();
        auditDocument.setBoxId(audit.getBoxId());
        auditDocument.setUser(audit.getUser());
        auditDocument.setAction(audit.getAction());
        auditDocument.setStatus(audit.getStatus());
        auditDocument.setFilename(audit.getFilename());
        auditDocument.setFileSizeMBytes(audit.getFileSizeMBytes());
        auditDocument.setDetails(audit.getDetails());
        auditDocument.setTimestamp(audit.getTimestamp());
        return repository.save(auditDocument).map(this::toEntity);
    }
    public Flux<Audit> findAll() {
        return repository.findAll().map(this::toEntity);
    }

}
