package co.com.bancolombia.usecase.openbox;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class OpenBoxUseCase {
    private final BoxRepository boxRepository;

    public OpenBoxUseCase(BoxRepository boxRepository){
        this.boxRepository = boxRepository;
    }


    public Mono<Box> openBox(String boxId, BigDecimal openingAmount) {
        return boxRepository.findById(boxId)
                .flatMap(box -> {
                    box.open(openingAmount);
                    return boxRepository.save(box);
                });
    }
}
