package co.com.bancolombia.mongo;

import co.com.bancolombia.mongo.model.AuditDocument;
import co.com.bancolombia.mongo.model.BoxDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;

public interface MongoDBAuditRepository extends ReactiveMongoRepository<AuditDocument, String>, ReactiveQueryByExampleExecutor<AuditDocument> {
}
