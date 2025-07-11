package co.com.bancolombia.usecase.openbox;

import co.com.bancolombia.model.box.Box;
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

class OpenBoxUseCaseTest {

    @Mock
    private BoxRepository boxRepository;

    @InjectMocks
    private OpenBoxUseCase openBoxUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void openBox_success() {
        String boxId = "box123";
        BigDecimal openingAmount = new BigDecimal("1000.00");
        Box box = new Box();
        box.setId(boxId);

        when(boxRepository.findById(boxId)).thenReturn(Mono.just(box));
        when(boxRepository.save(any(Box.class))).thenReturn(Mono.just(box));

        StepVerifier.create(openBoxUseCase.openBox(boxId, openingAmount))
                .expectNextMatches(openedBox -> openedBox.getId().equals(boxId))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verify(boxRepository).save(any(Box.class));
    }

    @Test
    void openBox_boxNotFound() {
        String boxId = "box123";
        BigDecimal openingAmount = new BigDecimal("1000.00");

        when(boxRepository.findById(boxId)).thenReturn(Mono.empty());

        StepVerifier.create(openBoxUseCase.openBox(boxId, openingAmount))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
        verify(boxRepository, never()).save(any(Box.class));
    }

    @Test
    void openBox_saveError() {
        String boxId = "box123";
        BigDecimal openingAmount = new BigDecimal("1000.00");
        Box box = new Box();
        box.setId(boxId);

        when(boxRepository.findById(boxId)).thenReturn(Mono.just(box));
        when(boxRepository.save(any(Box.class))).thenReturn(Mono.error(new RuntimeException("Save error")));

        StepVerifier.create(openBoxUseCase.openBox(boxId, openingAmount))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Save error"))
                .verify();

        verify(boxRepository).findById(boxId);
        verify(boxRepository).save(any(Box.class));
    }
}