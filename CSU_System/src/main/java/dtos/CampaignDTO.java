package dtos;

import dtos.survey.SurveyDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "DTO representing a campaign with its metadata and linked entity IDs")
public class CampaignDTO extends GenericDTO {

    @Schema(description = "Name of the campaign", example = "Operativo Invierno 2025")
    private String name;

    @Schema(description = "Start date of the campaign", example = "2025-06-01")
    private LocalDate startDate;

    @Schema(description = "End date of the campaign", example = "2025-07-31")
    private LocalDate endDate;

    @Schema(description = "ID of the neighborhood where the campaign takes place", example = "12")
    private Long neighborhoodId;

    @Schema(description = "Survey associated with the campaign")
    private SurveyDTO survey;


    public CampaignDTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getNeighborhoodId() {
        return neighborhoodId;
    }

    public void setNeighborhoodId(Long neighborhoodId) {
        this.neighborhoodId = neighborhoodId;
    }

    public SurveyDTO getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyDTO survey) {
        this.survey = survey;
    }
}
