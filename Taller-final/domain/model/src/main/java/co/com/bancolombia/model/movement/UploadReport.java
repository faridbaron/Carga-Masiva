package co.com.bancolombia.model.movement;

import java.time.LocalDateTime;

public class UploadReport {

    private String boxId;
    private Integer total;
    private Integer success;
    private Integer failed;
    private LocalDateTime uploadedAt;
    private String uploadedBy;

    public UploadReport(String boxId, Integer total, Integer success, Integer failed, LocalDateTime uploadedAt, String uploadedBy) {
        this.boxId = boxId;
        this.total = total;
        this.success = success;
        this.failed = failed;
        this.uploadedAt = uploadedAt;
        this.uploadedBy = uploadedBy;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getFailed() {
        return failed;
    }

    public void setFailed(Integer failed) {
        this.failed = failed;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

}
