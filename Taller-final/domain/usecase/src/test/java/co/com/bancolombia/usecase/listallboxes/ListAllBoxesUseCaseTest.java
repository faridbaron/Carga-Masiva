package co.com.bancolombia.usecase.listallboxes;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

class ListAllBoxesUseCaseTest {

    @Mock
    private BoxRepository boxRepository;

    @InjectMocks
    private ListAllBoxesUseCase listAllBoxesUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listAllBoxes_success() {
        Box box1 = new Box();
        box1.setId("box1");
        box1.setName("Test Box 1");

        Box box2 = new Box();
        box2.setId("box2");
        box2.setName("Test Box 2");

        when(boxRepository.findAll()).thenReturn(Flux.fromIterable(List.of(box1, box2)));

        StepVerifier.create(listAllBoxesUseCase.listAllBoxes())
                .expectNext(box1)
                .expectNext(box2)
                .verifyComplete();

        verify(boxRepository).findAll();
    }

    @Test
    void listAllBoxes_noBoxesFound() {
        when(boxRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(listAllBoxesUseCase.listAllBoxes())
                .verifyComplete();

        verify(boxRepository).findAll();
    }
}