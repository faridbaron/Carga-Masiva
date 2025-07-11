package co.com.bancolombia.model.events.model;

import co.com.bancolombia.model.box.Box;

import java.time.LocalDateTime;

public class BoxReopenedEvent {

    private final Box box;
    private final String responsibleUser;
    private final LocalDateTime reopenedAt;

    public BoxReopenedEvent(Box box, String responsibleUser, LocalDateTime reopenedAt) {
        this.box = box;
        this.responsibleUser = responsibleUser;
        this.reopenedAt = reopenedAt;
    }

    public Box getBox() {
        return box;
    }
    public String getResponsibleUser() {
        return responsibleUser;
    }
    public LocalDateTime getReopenedAt() {
        return reopenedAt;
    }
}
