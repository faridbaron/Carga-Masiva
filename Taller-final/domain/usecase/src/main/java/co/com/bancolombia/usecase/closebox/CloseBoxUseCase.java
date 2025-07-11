package co.com.bancolombia.usecase.closebox;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public class CloseBoxUseCase {
    private final BoxRepository boxRepository;

    public CloseBoxUseCase(BoxRepository boxRepository){
        this.boxRepository = boxRepository;
    }

    public Mono<Box> closeBox(String boxId, BigDecimal closingAmount) {
        return boxRepository.findById(boxId)
                .flatMap(box -> {
                    box.close(closingAmount);
                    return boxRepository.save(box);
                });
    }
}
