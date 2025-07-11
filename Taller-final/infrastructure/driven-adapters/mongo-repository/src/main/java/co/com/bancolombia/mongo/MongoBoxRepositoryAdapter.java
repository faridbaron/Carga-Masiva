package co.com.bancolombia.mongo;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import co.com.bancolombia.mongo.helper.AdapterOperations;
import co.com.bancolombia.mongo.model.BoxDocument;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class MongoBoxRepositoryAdapter extends AdapterOperations<Box, BoxDocument, String, MongoDBBoxRepository>
 implements BoxRepository
{

    public MongoBoxRepositoryAdapter(MongoDBBoxRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Box.class));
    }

    public Mono<Box> findById(String id){
        return repository.findById(id).map(this::toEntity);
    }

    public Mono<Box> save(Box box){
        BoxDocument boxObject = new BoxDocument();
        boxObject.setName(box.getName());
        boxObject.setClosedAt(box.getClosedAt());
        boxObject.setId(box.getId());
        boxObject.setClosingAmount(box.getClosingAmount());
        boxObject.setOpenedAt(box.getOpenedAt());
        boxObject.setStatus(box.getStatus());
        boxObject.setCurrentBalance(box.getCurrentBalance());
        boxObject.setClosedAt(box.getClosedAt());
        return repository.save(boxObject).map(this::toEntity);
    }

    public Flux<Box> findAll() {
        return repository.findAll().map(this::toEntity);
    }
}
