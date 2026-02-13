package dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
import java.time.LocalDate;

@Schema(
        description = "Data Transfer Object representing a generated report",
        requiredProperties = {"name", "description", "authorId"}
)
public class ReportDTO extends GenericDTO {

    @Schema(description = "Name of the report", example = "Annual Health Statistics")
    private String name;

    @Schema(description = "Description of the report", example = "Summary of annual health statistics for the region")
    private String description;

    @Schema(description = "ID of the health staff member who authored the report", example = "5")
    private Long authorId;

    @Schema(description = "Full name of the health staff member who authored the report", example = "John Doe")
    private String authorName;

    @Schema(description = "Indicates if the report is visible to all health staff", example = "true")
    private boolean visibleToAllHealthStaff;

    @Schema(description = "IDs of social organization representatives with whom the report is shared")
    private Set<Long> sharedWithIds;

    @Schema(description = "Indicates if the report has an associated file", example = "true")
    private boolean hasFile;

    @Schema(description = "Name of the file associated with the report", example = "report_2025.pdf")
    private String fileName;

    @Schema(description = "MIME type of the file associated with the report", example = "application/pdf")
    private String fileMime;

    @Schema(description = "Download URL for the report file", example = "/api/reports/15/file")
    private String downloadUrl;

    @Schema(description = "Whether this report is public for all users", example = "false")
    private boolean publicVisible;

    @Schema(description = "Creation timestamp of the report (from BaseEntity)")
    private LocalDate createdDate;

    public ReportDTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public boolean isVisibleToAllHealthStaff() {
        return visibleToAllHealthStaff;
    }

    public void setVisibleToAllHealthStaff(boolean visibleToAllHealthStaff) {
        this.visibleToAllHealthStaff = visibleToAllHealthStaff;
    }

    public Set<Long> getSharedWithIds() {
        return sharedWithIds;
    }

    public void setSharedWithIds(Set<Long> sharedWithIds) {
        this.sharedWithIds = sharedWithIds;
    }

    public boolean isHasFile() {
        return hasFile;
    }

    public void setHasFile(boolean hasFile) {
        this.hasFile = hasFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMime() {
        return fileMime;
    }

    public void setFileMime(String fileMime) {
        this.fileMime = fileMime;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public boolean isPublicVisible() { return publicVisible; }
    public void setPublicVisible(boolean publicVisible) { this.publicVisible = publicVisible; }

    public java.time.LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(java.time.LocalDate createdDate) { this.createdDate = createdDate; }


}
