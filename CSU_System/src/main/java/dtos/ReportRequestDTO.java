package dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Report request DTO")
public class ReportRequestDTO extends GenericDTO {

    @Schema(description = "Report ID if completed")
    private Long reportId;

    @Schema(description = "Requester user ID (representative)")
    private Long requesterId;

    @Schema(description = "Requester full name")
    private String requesterName;

    @Schema(description = "Status: PENDING, COMPLETED, REJECTED")
    private String status;

    @Schema(description = "Free text")
    private String description;

    @Schema(description = "Resolver HealthStaff ID (completed or rejected)")
    private Long resolvedById;

    @Schema(description = "Resolver HealthStaff full name")
    private String resolvedByName;

    @Schema(description = "Resolution timestamp")
    private LocalDateTime resolvedAt;

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }
    public String getRequesterName() { return requesterName; }
    public void setRequesterName(String requesterName) { this.requesterName = requesterName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getResolvedById() { return resolvedById; }
    public void setResolvedById(Long resolvedById) { this.resolvedById = resolvedById; }
    public String getResolvedByName() { return resolvedByName; }
    public void setResolvedByName(String resolvedByName) { this.resolvedByName = resolvedByName; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}
