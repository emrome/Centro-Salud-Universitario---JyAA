package dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(
        description = "Data Transfer Object for a scheduled event involving surveyors in a specific zone",
        requiredProperties = {"date", "zoneId"}
)
public class EventDTO extends GenericDTO {

    @Schema(description = "Date of the event", example = "2025-07-01")
    private LocalDate date;

    @Schema(description = "ID of the zone where the event takes place", example = "12")
    private Long zoneId;

    @Schema(description = "List of surveyors assigned to this event")
    private List<Long> surveyorsIds;

    public EventDTO() {}

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public List<Long> getSurveyorIds() {
        return surveyorsIds;
    }

    public void setSurveyorIds(List<Long> surveyorIds) {
        this.surveyorsIds = surveyorIds;
    }
}
