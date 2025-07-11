package co.com.bancolombia.usecase.listallboxes;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import reactor.core.publisher.Flux;



public class ListAllBoxesUseCase {

    private final BoxRepository boxRepository;

    public ListAllBoxesUseCase(BoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    public Flux<Box> listAllBoxes() {
        return boxRepository.findAll();
    }
}
