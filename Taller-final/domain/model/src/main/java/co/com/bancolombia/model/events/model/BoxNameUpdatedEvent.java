package co.com.bancolombia.model.events.model;

import java.time.LocalDateTime;

public class BoxNameUpdatedEvent {
    private final String boxId;
    private final String oldName;
    private final String newName;
    private final LocalDateTime updatedAt;
    private final String responsibleUser;
    public BoxNameUpdatedEvent(String boxId, String oldName, String newName, LocalDateTime updatedAt, String responsibleUser) {
        this.boxId = boxId;
        this.oldName = oldName;
        this.newName = newName;
        this.updatedAt = updatedAt;
        this.responsibleUser = responsibleUser;
    }

    public String getBoxId() {
        return boxId;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public String getResponsibleUser() {
        return responsibleUser;
    }





}