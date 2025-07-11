package co.com.bancolombia.usecase.closebox;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.BoxStatus;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CloseBoxUseCaseTest {

    @Mock
    private BoxRepository boxRepository;

    @InjectMocks
    private CloseBoxUseCase closeBoxUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void closeBox_success() {
        String boxId = "box123";
        BigDecimal closingAmount = new BigDecimal("1000.00");
        Box box = new Box();
        box.setId(boxId);
        box.setStatus(BoxStatus.OPENED);

        when(boxRepository.findById(boxId)).thenReturn(Mono.just(box));
        when(boxRepository.save(any(Box.class))).thenReturn(Mono.just(box));

        StepVerifier.create(closeBoxUseCase.closeBox(boxId, closingAmount))
                .expectNextMatches(closedBox -> closedBox.getId().equals(boxId))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verify(boxRepository).save(any(Box.class));
    }
}