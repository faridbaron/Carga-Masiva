package co.com.bancolombia.usecase.getboxbyid;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.box.gateways.BoxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class GetBoxByIdUseCaseTest {

    @Mock
    private BoxRepository boxRepository;

    @InjectMocks
    private GetBoxByIdUseCase getBoxByIdUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getBoxById_success() {
        String boxId = "box123";
        Box box = new Box();
        box.setId(boxId);
        box.setName("Test Box");

        when(boxRepository.findById(boxId)).thenReturn(Mono.just(box));

        StepVerifier.create(getBoxByIdUseCase.getBoxById(boxId))
                .expectNextMatches(retrievedBox -> retrievedBox.getId().equals(boxId) && retrievedBox.getName().equals("Test Box"))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
    }

    @Test
    void getBoxById_notFound() {
        String boxId = "box123";

        when(boxRepository.findById(boxId)).thenReturn(Mono.empty());

        StepVerifier.create(getBoxByIdUseCase.getBoxById(boxId))
                .verifyComplete();

        verify(boxRepository).findById(boxId);
    }
}