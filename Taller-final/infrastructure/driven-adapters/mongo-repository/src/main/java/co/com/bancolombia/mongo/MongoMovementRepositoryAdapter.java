package co.com.bancolombia.mongo;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.model.movement.Movement;
import co.com.bancolombia.model.movement.gateways.MovementRepository;
import co.com.bancolombia.mongo.helper.AdapterOperations;
import co.com.bancolombia.mongo.model.BoxDocument;
import co.com.bancolombia.mongo.model.MovementDocument;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MongoMovementRepositoryAdapter extends AdapterOperations<Movement, MovementDocument, String, MongoDBMovementRepository>
 implements MovementRepository
{

    public MongoMovementRepositoryAdapter(MongoDBMovementRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Movement.class));
    }


    public Mono<Movement> save(Movement movement){
        MovementDocument movementDocument = new MovementDocument();
        movementDocument.setMovementId(movement.getMovementId());
        movementDocument.setBoxId(movement.getBoxId());
        movementDocument.setAmount(movement.getAmount());
        movementDocument.setType(movement.getType());
        movementDocument.setDate(movement.getDate());
        movementDocument.setCurrency(movement.getCurrency());
        movementDocument.setDescription(movement.getDescription());
        return repository.save(movementDocument).map(this::toEntity);
    }

}
