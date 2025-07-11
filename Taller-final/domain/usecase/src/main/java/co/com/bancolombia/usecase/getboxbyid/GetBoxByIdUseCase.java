package co.com.bancolombia.usecase.getboxbyid;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import reactor.core.publisher.Mono;

public class GetBoxByIdUseCase {

    private final BoxRepository boxRepository;

    public GetBoxByIdUseCase(BoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    public Mono<Box> getBoxById(String boxId) {
        return boxRepository.findById(boxId);
    }
}