package co.com.bancolombia.api;

import co.com.bancolombia.model.audit.Audit;
import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.model.movement.UploadReport;
import co.com.bancolombia.usecase.audit.AuditUseCase;
import co.com.bancolombia.usecase.closebox.CloseBoxUseCase;
import co.com.bancolombia.usecase.createbox.CreateBoxUseCase;
import co.com.bancolombia.usecase.deletebox.DeleteBoxUseCase;
import co.com.bancolombia.usecase.getboxbyid.GetBoxByIdUseCase;
import co.com.bancolombia.usecase.gethistoryaudit.GetHistoryAuditUseCase;
import co.com.bancolombia.usecase.listallboxes.ListAllBoxesUseCase;
import co.com.bancolombia.usecase.openbox.OpenBoxUseCase;
import co.com.bancolombia.usecase.reopenbox.ReopenBoxUseCase;
import co.com.bancolombia.usecase.updateboxname.UpdateBoxNameUseCase;
import co.com.bancolombia.usecase.uploadmovements.UploadMovementsUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebFluxTest
@ContextConfiguration(classes = {RouterRest.class, BoxHandler.class, MovementHandler.class, AuditHandler.class})
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CreateBoxUseCase createBoxUseCase;

    @MockitoBean
    private OpenBoxUseCase openBoxUseCase;

    @MockitoBean
    private CloseBoxUseCase closeBoxUseCase;

    @MockitoBean
    private GetBoxByIdUseCase getBoxByIdUseCase;

    @MockitoBean
    private ListAllBoxesUseCase listAllBoxesUseCase;

    @MockitoBean
    private UpdateBoxNameUseCase updateBoxNameUseCase;

    @MockitoBean
    private DeleteBoxUseCase deleteBoxUseCase;

    @MockitoBean
    private ReopenBoxUseCase reopenBoxUseCase;

    @MockitoBean
    private UploadMovementsUseCase uploadMovementsUseCase;

    @MockitoBean
    private AuditUseCase auditUseCase;

    @MockitoBean
    private GetHistoryAuditUseCase getHistoryAuditUseCase;

    @BeforeEach
    void setUp() {
        Mockito.reset(createBoxUseCase, openBoxUseCase, closeBoxUseCase, getBoxByIdUseCase, listAllBoxesUseCase,
                updateBoxNameUseCase, deleteBoxUseCase, reopenBoxUseCase, uploadMovementsUseCase, auditUseCase, getHistoryAuditUseCase);
    }

    @Test
    void testListAllBoxes() {
        Box box1 = new Box();
        box1.setId("box1");
        box1.setName("Test Box 1");

        Box box2 = new Box();
        box2.setId("box2");
        box2.setName("Test Box 2");

        Mockito.when(listAllBoxesUseCase.listAllBoxes()).thenReturn(Flux.fromIterable(List.of(box1, box2)));

        webTestClient.get()
                .uri("/api/boxes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Box.class)
                .consumeWith(response -> {
                    List<Box> boxes = response.getResponseBody();
                    assert boxes != null;
                    assertEquals(2, boxes.size());
                    assertEquals("box1", boxes.get(0).getId());
                    assertEquals("Test Box 1", boxes.get(0).getName());
                    assertEquals("box2", boxes.get(1).getId());
                    assertEquals("Test Box 2", boxes.get(1).getName());
                });

        Mockito.verify(listAllBoxesUseCase).listAllBoxes();
    }

    @Test
    void testCreateBox() {
        Box box = new Box();
        box.setId("box123");
        box.setName("New Box");

        Mockito.when(createBoxUseCase.createBox("box123", "New Box")).thenReturn(Mono.just(box));

        webTestClient.post()
                .uri("/api/boxes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(box)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Box.class)
                .consumeWith(response -> {
                    Box responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assertEquals("box123", responseBody.getId());
                    assertEquals("New Box", responseBody.getName());
                });

        Mockito.verify(createBoxUseCase).createBox("box123", "New Box");
    }

    @Test
    void testUploadMovements() {
        UploadReport report = new UploadReport("box123",10, 8, 2, LocalDateTime.now(), "user123");

        String csvContent = """
        movementId,boxId,date,type,amount,currency,description
        1,BOX-001,2024-06-30T08:30:00,INCOME,150000,PER,Ingreso apertura
        """;

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder
                .part("file", new ByteArrayResource(csvContent.getBytes()) {
                    @Override
                    public String getFilename() {
                        return "test.csv";
                    }
                })
                .header("Content-Type", "text/csv");

        Mockito.when(uploadMovementsUseCase.uploadCSV(Mockito.anyString(), Mockito.any(), Mockito.anyString()))
                .thenReturn(Mono.just(report));
        Mockito.when(auditUseCase.logUpload(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt()))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/boxes/box123/movements/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    String responseBody = new String(response.getResponseBody());
                    assertTrue(responseBody.contains("\"boxId\":\"box123\""));
                    assertTrue(responseBody.contains("\"total\":10"));
                    assertTrue(responseBody.contains("\"success\":8"));
                    assertTrue(responseBody.contains("\"failed\":2"));
                    assertTrue(responseBody.contains("\"uploadedBy\":\"user123\""));
                });

        Mockito.verify(uploadMovementsUseCase).uploadCSV(Mockito.anyString(), Mockito.any(), Mockito.anyString());
        Mockito.verify(auditUseCase).logUpload(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyInt());
    }

    @Test
    void testGetAllHistoryAudit() {
        Audit audit1 = new Audit("box1", "user1", "UPLOAD", "SUCCESS", "file1.csv", 1, "Details1", LocalDateTime.now());
        Audit audit2 = new Audit("box2", "user2", "UPLOAD", "FAILURE", "file2.csv", 2, "Details2", LocalDateTime.now());

        Mockito.when(getHistoryAuditUseCase.getHistory()).thenReturn(Flux.fromIterable(List.of(audit1, audit2)));

        webTestClient.get()
                .uri("/api/upload/audit/history")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Audit.class)
                .consumeWith(response -> {
                    List<Audit> audits = response.getResponseBody();
                    assert audits != null;
                    assertEquals(2, audits.size());
                    assertEquals("box1", audits.get(0).getBoxId());
                    assertEquals("box2", audits.get(1).getBoxId());
                });

        Mockito.verify(getHistoryAuditUseCase).getHistory();
    }

}