package co.com.bancolombia.model.events.model;

import java.time.LocalDateTime;

public class BoxDeletedEvent {

    private final String boxId;
    private final String name;
    private final String responsibleUser;
    private final LocalDateTime deletedAt;

    public BoxDeletedEvent(String boxId, String name, String responsibleUser, LocalDateTime deletedAt) {
        this.boxId = boxId;
        this.name = name;
        this.responsibleUser = responsibleUser;
        this.deletedAt = deletedAt;
    }



    public String getBoxId() {
        return boxId;
    }
    public String getName() {
        return name;
    }
    public String getResponsibleUser() {
        return responsibleUser;
    }
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
}
