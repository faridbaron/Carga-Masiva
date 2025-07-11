package co.com.bancolombia.mongo;

import co.com.bancolombia.mongo.model.BoxDocument;
import co.com.bancolombia.mongo.model.MovementDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;

public interface MongoDBMovementRepository extends ReactiveMongoRepository<MovementDocument, String>, ReactiveQueryByExampleExecutor<MovementDocument> {
}
