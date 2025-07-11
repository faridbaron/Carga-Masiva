package co.com.bancolombia.model.audit;

import java.time.LocalDateTime;

public class Audit {
    private String boxId;
    private String user;
    private String action;
    private String status;
    private String filename;
    private long fileSizeMBytes;
    private String details;
    private LocalDateTime timestamp;

    public Audit() {
    }
    public Audit(String boxId, String user, String action, String status, String filename, long fileSizeMBytes, String details, LocalDateTime timestamp) {
        this.boxId = boxId;
        this.user = user;
        this.action = action;
        this.status = status;
        this.filename = filename;
        this.fileSizeMBytes = fileSizeMBytes;
        this.details = details;
        this.timestamp = timestamp;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getFileSizeMBytes() {
        return fileSizeMBytes;
    }

    public void setFileSizeMBytes(long fileSizeMBytes) {
        this.fileSizeMBytes = fileSizeMBytes;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }


}
