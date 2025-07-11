package co.com.bancolombia.api;

import co.com.bancolombia.model.box.Box;
import co.com.bancolombia.usecase.closebox.CloseBoxUseCase;
import co.com.bancolombia.usecase.createbox.CreateBoxUseCase;
import co.com.bancolombia.usecase.deletebox.DeleteBoxUseCase;
import co.com.bancolombia.usecase.getboxbyid.GetBoxByIdUseCase;
import co.com.bancolombia.usecase.listallboxes.ListAllBoxesUseCase;
import co.com.bancolombia.usecase.openbox.OpenBoxUseCase;
import co.com.bancolombia.usecase.reopenbox.ReopenBoxUseCase;
import co.com.bancolombia.usecase.updateboxname.UpdateBoxNameUseCase;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class BoxHandler {

    private final CreateBoxUseCase createBoxUseCase;
    private final OpenBoxUseCase openBoxUseCase;
    private final CloseBoxUseCase closeBoxUseCase;
    private final GetBoxByIdUseCase getBoxByIdUseCase;
    private final ListAllBoxesUseCase listAllBoxesUseCase;
    private final UpdateBoxNameUseCase updateBoxNameUseCase;
    private final DeleteBoxUseCase deleteBoxUseCase;
    private final ReopenBoxUseCase reopenBoxUseCase;

    public BoxHandler(CreateBoxUseCase createBoxUseCase, OpenBoxUseCase openBoxUseCase, CloseBoxUseCase closeBoxUseCase, GetBoxByIdUseCase getBoxByIdUseCase, ListAllBoxesUseCase listAllBoxesUseCase, UpdateBoxNameUseCase updateBoxNameUseCase, DeleteBoxUseCase deleteBoxUseCase, ReopenBoxUseCase reopenBoxUseCase) {
        this.createBoxUseCase = createBoxUseCase;
        this.openBoxUseCase = openBoxUseCase;
        this.closeBoxUseCase = closeBoxUseCase;
        this.getBoxByIdUseCase = getBoxByIdUseCase;
        this.listAllBoxesUseCase = listAllBoxesUseCase;
        this.updateBoxNameUseCase = updateBoxNameUseCase;
        this.deleteBoxUseCase = deleteBoxUseCase;
        this.reopenBoxUseCase = reopenBoxUseCase;
    }


    public Mono<ServerResponse> createBox(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Box.class)
                .flatMap(box -> createBoxUseCase.createBox(box.getId(), box.getName()))
                .flatMap(currentBox -> ServerResponse.ok().body(BodyInserters.fromValue(currentBox)))
                .onErrorResume(e -> ServerResponse.status(500)
                        .bodyValue("Error creating box: " + e.getMessage()));
    }

    public Mono<ServerResponse> open(ServerRequest request) {
        String id = request.pathVariable("id");
        return openBoxUseCase.openBox(id, BigDecimal.ZERO).flatMap(box -> ServerResponse.ok()
                        .body(BodyInserters.fromValue(box)))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(e -> ServerResponse.status(500)
                        .bodyValue("Error open box: " + e.getMessage()));
    }

    public Mono<ServerResponse> close(ServerRequest request) {
        String id = request.pathVariable("id");
        return closeBoxUseCase.closeBox(id, BigDecimal.ZERO).flatMap(box -> ServerResponse.ok()
                        .body(BodyInserters.fromValue(box)))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(e -> ServerResponse.status(500)
                        .bodyValue("Error close box: " + e.getMessage()));
    }

    public Mono<ServerResponse> getByIdBox(ServerRequest request) {
        String id = request.pathVariable("id");
        return getBoxByIdUseCase.getBoxById(id)
                .flatMap(box -> ServerResponse.ok().body(BodyInserters.fromValue(box)))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> listAllBoxes(ServerRequest request) {
        return ServerResponse.ok()
                .body(BodyInserters.fromPublisher(listAllBoxesUseCase.listAllBoxes(), Box.class));
    }

    public Mono<ServerResponse> updateNameBox(ServerRequest request) {
        String boxId = request.pathVariable("id");
        String username = Optional.ofNullable(request.headers().firstHeader("X-User"))
                .filter(h -> !h.isEmpty())
                .orElse("anonymous");

        return request.bodyToMono(Box.class)
                .flatMap(box -> updateBoxNameUseCase.updateBoxName(boxId, box.getName(), username))
                .flatMap(updatedBox -> ServerResponse.ok().bodyValue(updatedBox))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(e -> ServerResponse.status(500)
                        .bodyValue("Error updating box name: " + e.getMessage()));
    }

    public Mono<ServerResponse> deleteBox(ServerRequest request) {
        String boxId = request.pathVariable("id");
        String username = Optional.ofNullable(request.headers().firstHeader("X-User"))
                .filter(h -> !h.isEmpty())
                .orElse("anonymous");

        return getBoxByIdUseCase.getBoxById(boxId)
                .flatMap(box -> deleteBoxUseCase.deleteBox(boxId, username))
                .flatMap(deletedBox -> ServerResponse.ok().bodyValue(deletedBox))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(e -> ServerResponse.status(500)
                        .bodyValue("Error deleting box: " + e.getMessage()));
    }

    public Mono<ServerResponse> reopenBox(ServerRequest request) {
        String boxId = request.pathVariable("id");
        String username = Optional.ofNullable(request.headers().firstHeader("X-User"))
                .filter(h -> !h.isEmpty())
                .orElse("anonymous");
        return reopenBoxUseCase.reopenBox(boxId, username)
                .flatMap(reopenedBox -> ServerResponse.ok().bodyValue(reopenedBox))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(e -> ServerResponse.status(500)
                        .bodyValue("Error reopening box: " + e.getMessage()));
    }
}
