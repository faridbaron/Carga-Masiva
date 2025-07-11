package co.com.bancolombia.usecase.audit;

import co.com.bancolombia.model.audit.Audit;
import co.com.bancolombia.model.audit.gateways.AuditRepository;
import co.com.bancolombia.model.movement.UploadReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuditUseCaseTest {

    @Mock
    private AuditRepository auditRepository;

    @InjectMocks
    private AuditUseCase auditUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void logUpload_successfulReport() {
        UploadReport report = new UploadReport("10", 10, 8,
                2, LocalDateTime.now(),"prueba");
        when(auditRepository.save(any(Audit.class))).thenReturn(Mono.empty());

        StepVerifier.create(auditUseCase.logUpload("box123", "user123", report, null, "file.csv", 2048))
                .verifyComplete();

        ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);
        verify(auditRepository).save(captor.capture());
        Audit audit = captor.getValue();

        assertEquals("box123", audit.getBoxId());
        assertEquals("user123", audit.getUser());
        assertEquals("UPLOAD", audit.getAction());
        assertEquals("SUCCESS", audit.getStatus());
        assertEquals("file.csv", audit.getFilename());
        assertEquals(0, audit.getFileSizeMBytes());
        assertEquals("Processed: 10, Success: 8, Errors: 2", audit.getDetails());
    }

    @Test
    void logUpload_withError() {
        Throwable error = new RuntimeException("Test error");
        when(auditRepository.save(any(Audit.class))).thenReturn(Mono.empty());

        StepVerifier.create(auditUseCase.logUpload("box123", "user123", null, error.getMessage(), "file.csv", 2048))
                .verifyComplete();

        ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);
        verify(auditRepository).save(captor.capture());
        Audit audit = captor.getValue();

        assertEquals("FAILURE", audit.getStatus());
        assertEquals("Error: Test error", audit.getDetails());
    }

    @Test
    void logUpload_nullReportNoError() {
        when(auditRepository.save(any(Audit.class))).thenReturn(Mono.empty());

        StepVerifier.create(auditUseCase.logUpload("box123", "user123", null, null, "file.csv", 2048))
                .verifyComplete();

        ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);
        verify(auditRepository).save(captor.capture());
        Audit audit = captor.getValue();

        assertEquals("SUCCESS", audit.getStatus());
        assertEquals("Unknown result", audit.getDetails());
    }

    @Test
    void logUpload_nullReportWithError() {
        Throwable error = new RuntimeException("Another error");
        when(auditRepository.save(any(Audit.class))).thenReturn(Mono.empty());

        StepVerifier.create(auditUseCase.logUpload("box123", "user123", null, error.getMessage(), "file.csv", 2048))
                .verifyComplete();

        ArgumentCaptor<Audit> captor = ArgumentCaptor.forClass(Audit.class);
        verify(auditRepository).save(captor.capture());
        Audit audit = captor.getValue();

        assertEquals("FAILURE", audit.getStatus());
        assertEquals("Error: Another error", audit.getDetails());
    }
}