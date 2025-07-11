package co.com.bancolombia.usecase.gethistoryaudit;

import co.com.bancolombia.model.audit.Audit;
import co.com.bancolombia.model.audit.gateways.AuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

class GetHistoryAuditUseCaseTest {

    @Mock
    private AuditRepository auditRepository;

    @InjectMocks
    private GetHistoryAuditUseCase getHistoryAuditUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getHistory_success() {
        Audit audit1 = new Audit("box1", "user1", "UPLOAD", "SUCCESS", "file1.csv", 1, "Details1", LocalDateTime.now());
        Audit audit2 = new Audit("box2", "user2", "UPLOAD", "FAILURE", "file2.csv", 2, "Details2", LocalDateTime.now());

        when(auditRepository.findAll()).thenReturn(Flux.fromIterable(List.of(audit1, audit2)));

        StepVerifier.create(getHistoryAuditUseCase.getHistory())
                .expectNext(audit1)
                .expectNext(audit2)
                .verifyComplete();

        verify(auditRepository, times(1)).findAll();
    }
}